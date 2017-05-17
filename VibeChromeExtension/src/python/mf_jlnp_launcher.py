#!/usr/bin/env python

import os;
import sys;
import struct;
import threading;
import Queue;
import json;

if sys.platform == 'win32':
	import msvcrt
	msvcrt.setmode(sys.stdin.fileno(),os.O_BINARY)
	msvcrt.setmode(sys.stdout.fileno(),os.O_BINARY)
	
def send_message(message):
	sys.stdout.write(struct.pack('I',len(message)))
	sys.stdout.write(message)
	sys.stdout.flush()
	
def read_message(queue):
	try:
		message_number = 0
		while 1:
			text_length_in_bytes=sys.stdin.read(4)
			
			if len(text_length_in_bytes) == 0:
				if queue:
					queue.put(None)
				sys.exit(0)
				
			text_length = struct.unpack('i',text_length_in_bytes)[0]
			text=sys.stdin.read(text_length).decode('utf-8')
			
			decodedText=json.loads(text)
			url=decodedText["url"]
			doindex=url.find('do?')
			sessionindex=url.find('jsessionid=')
			sanitizedUrl="{};{}{}".format(url[0:doindex+2],url[sessionindex:],url[doindex+2:sessionindex-1])
			result=os.system("javaws \"{}\"".format(sanitizedUrl))
	except (Exception) as e:
		pass
		
def main():	
	send_message('"Starting"')
	read_message(None)
	send_message('"Complete"')
	sys.exit(0)
	
if __name__=="__main__":
	main()
		
			