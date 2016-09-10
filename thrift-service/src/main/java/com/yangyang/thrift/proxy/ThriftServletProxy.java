package com.yangyang.thrift.proxy;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("serial")
public class ThriftServletProxy extends HttpServlet {

    private final TProcessor processor;

    private final TProtocolFactory inProtocolFactory;

    private final TProtocolFactory outProtocolFactory;

    private final Collection<Map.Entry<String, String>> customHeaders;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ThriftServletProxy(String serviceInterface, String serviceIface,
                              Object serviceImplObject) throws Exception {
        super();


        Class Processor = Class.forName(serviceInterface + "$Processor");
        Class Iface = Class
                .forName(StringUtils.hasText(serviceIface) ? serviceIface
                        : serviceInterface + "$Iface");
        Constructor con = Processor.getConstructor(Iface);
        TProcessor processor = (TProcessor) con.newInstance(serviceImplObject);

        this.processor = processor;
        this.inProtocolFactory = new TCompactProtocol.Factory();
        this.outProtocolFactory = new TCompactProtocol.Factory();
        this.customHeaders = new ArrayList<>();

    }

    public ThriftServletProxy(String serviceInterface,
                              Object serviceImplObject) throws Exception {
        this(serviceInterface, null, serviceImplObject);

    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ThriftServletProxy(TProcessor processor,
                              TProtocolFactory inProtocolFactory,
                              TProtocolFactory outProtocolFactory) {
        super();
        this.processor = processor;
        this.inProtocolFactory = inProtocolFactory;
        this.outProtocolFactory = outProtocolFactory;
        this.customHeaders = new ArrayList<Map.Entry<String, String>>();
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ThriftServletProxy(TProcessor processor,
                              TProtocolFactory protocolFactory) {
        this(processor, protocolFactory, protocolFactory);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        TTransport inTransport;
        TTransport outTransport;

        TProtocolFactory inProtocolFactory;
        TProtocolFactory outProtocolFactory;

        try {

            //application/vnd.apache.thrift.json; charset=utf-8
            String requestContentType = request.getContentType();
            if (requestContentType.startsWith("application/vnd.apache.thrift.json")) {
                inProtocolFactory = new TJSONProtocol.Factory();
                outProtocolFactory = new TJSONProtocol.Factory();
                response.setContentType("application/json");
            } else {
                inProtocolFactory = new TCompactProtocol.Factory();
                outProtocolFactory = new TCompactProtocol.Factory();
                response.setContentType("application/x-thrift");
            }

            if (null != this.customHeaders) {
                for (Map.Entry<String, String> header : this.customHeaders) {
                    response.addHeader(header.getKey(), header.getValue());
                }
            }
            InputStream in = request.getInputStream();
            OutputStream out = response.getOutputStream();

            TTransport transport = new TIOStreamTransport(in, out);
            inTransport = transport;
            outTransport = transport;

            TProtocol inProtocol = inProtocolFactory.getProtocol(inTransport);
            TProtocol outProtocol = outProtocolFactory.getProtocol(outTransport);

            processor.process(inProtocol, outProtocol);
            out.flush();
        } catch (TException te) {
            throw new ServletException(te);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void addCustomHeader(final String key, final String value) {
        this.customHeaders.add(new Map.Entry<String, String>() {
            public String getKey() {
                return key;
            }

            public String getValue() {
                return value;
            }

            public String setValue(String value) {
                return null;
            }
        });
    }

    public void setCustomHeaders(Collection<Map.Entry<String, String>> headers) {
        this.customHeaders.clear();
        this.customHeaders.addAll(headers);
    }

}
