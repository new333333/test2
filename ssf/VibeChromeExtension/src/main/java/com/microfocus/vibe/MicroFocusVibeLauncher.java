package com.microfocus.vibe;

import java.io.*;
import java.util.*;
import java.util.logging.*;

class MicroFocusVibeLauncher{
	private Logger logger = Logger.getLogger(MicroFocusVibeLauncher.class.getName());
	
	public static void main(String...args) throws IOException {
		MicroFocusVibeLauncher vibeLauncher=new MicroFocusVibeLauncher();		
	}
	
	public MicroFocusVibeLauncher(){
		initLogger();
		logger.info("Starting Read Message");
		readMessage();
	}
	
	public byte[] getBytes(int length){
		byte[] bytes=new byte[4];
		bytes[0]=(byte) (length & 0xFF);
		bytes[1]=(byte) ((length >> 8) & 0XFF);
		bytes[2]=(byte) ((length >> 16) & 0xFF);
		bytes[3]=(byte) ((length >> 24) & 0XFF);
		return bytes;
	}
	
	public int getInt(byte[] bytes){
		int value=0;
		for(int i=0;i<4;i++){
			int shift=i*8;
			value+=(bytes[i] & 0X000000FF) << shift ;
		}
		return value;
	}
	
	public void initLogger(){
		try{
			FileHandler fileHandler=new FileHandler("c:\\MicroFocus\\debug.log");
			logger.addHandler(fileHandler);
			SimpleFormatter simpleFormatter=new SimpleFormatter();
			fileHandler.setFormatter(simpleFormatter);					
		}
		catch(SecurityException securityException){
			securityException.printStackTrace();
			//TODO
		}
		catch(IOException ie){
			ie.printStackTrace();
			//TODO
		}
	}
	
	public void writeMessage(String message){
		try{
			message="{\"message\":\""+message+"\"}";
			System.out.write(getBytes(message.length()),0,4);
			byte[] messageBytes=message.getBytes();
			System.out.write(messageBytes,0,messageBytes.length);
			System.out.flush();
		}
		catch(Exception exception){
			logger.info("Exception in write message "+exception.getMessage());
		}
	}
	
	public void readMessage(){
		try{
			byte[] messageLength=new byte[4];
			System.in.read(messageLength,0,4);
			
			int textLength=getInt(messageLength);
			logger.info("Text Length is "+textLength);
			
			if(textLength == 0){
				logger.info("Exiting due to zero message length");
				System.exit(1);
			}
			
			byte[] textInBytes=new byte[textLength];
			System.in.read(textInBytes,0,textLength);
			String receivedText=new String(textInBytes);
			int jsonColonIndex=receivedText.indexOf(":",0);
			if(jsonColonIndex >= 0){
				String urlText=receivedText.substring(jsonColonIndex+2,receivedText.length()-1);
				int doIndex=urlText.indexOf("do?");
				if(doIndex<0){
					logger.info("Failed to parse received url string.  Exiting the application");
					System.exit(1);
				}
				int sessionIndex=urlText.indexOf("jsessionid=");
				if(sessionIndex<0){
					logger.info("Failed to parse received url string.  Exiting the application");
					System.exit(1);					
				}
				urlText=urlText.substring(0,doIndex+2)+";"+urlText.substring(sessionIndex)+urlText.substring(doIndex+2,sessionIndex-1);
				Runtime runtime=Runtime.getRuntime();
				Process result=runtime.exec("javaws \""+urlText+"\"");
				result.waitFor();
				logger.info("Executing Result "+result.exitValue());
				writeMessage("Process Completed");
			}
			else{
				logger.info("Invalid message format recevied.  Exiting the application");
				System.exit(0);
			}
		}
		catch(InterruptedException interruptedException){
			logger.info(interruptedException.getMessage());
		}
		catch(IOException ioException){
			logger.info(ioException.getMessage());
		}
	}
}