package com.yangyang.exception;

public class LockException extends RuntimeException{
	private static final long serialVersionUID = -8127990086855994884L;
	public LockException(String e){
         super(e);
     }
     public LockException(Exception e){
         super(e);
     }

}
