import socket, sys

from django.shortcuts import render_to_response, get_object_or_404
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.conf import settings

def home(request):
    return render_to_response('ui/home.html', {}, context_instance=RequestContext(request))

def cmd(request, cmd, value=0):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    try:
        sock.connect((settings.SERVER_NAME, settings.SERVER_PORT))
        message = sock.recv(1024)
        sock.sendall("CONTROL " + settings.SPYBOT_NAME + " " + settings.SPYBOT_PASSPHRASE + "\n")
        message = sock.recv(1024)
        if cmd == "left":
            sock.sendall("SEND LEFTMOTOR 255\n")
            message = sock.recv(1024)
            sock.sendall("SEND RIGHTMOTOR 0\n")
            message = sock.recv(1024)
        elif cmd == "right":
            sock.sendall("SEND LEFTMOTOR 0\n")
            message = sock.recv(1024)
            sock.sendall("SEND RIGHTMOTOR 255\n")
            message = sock.recv(1024)
        elif cmd == "stop":
            sock.sendall("SEND LEFTMOTOR 128\n")
            message = sock.recv(1024)
            sock.sendall("SEND RIGHTMOTOR 128\n")
            message = sock.recv(1024)
        elif cmd == "forward":
            sock.sendall("SEND LEFTMOTOR 0\n")
            message = sock.recv(1024)
            sock.sendall("SEND RIGHTMOTOR 0\n")
            message = sock.recv(1024)
        elif cmd == "reverse":
            sock.sendall("SEND LEFTMOTOR 255\n")
            message = sock.recv(1024)
            sock.sendall("SEND RIGHTMOTOR 255\n")
            message = sock.recv(1024)
        elif cmd == "tilt":
            sock.sendall("SEND SERVO " + value + "\n")
            message = sock.recv(1024)
            
        sock.sendall("TERMINATE\n")
        
    finally:
        sock.close()
        
    return render_to_response('ui/message.html', { "message" : message }, context_instance=RequestContext(request))