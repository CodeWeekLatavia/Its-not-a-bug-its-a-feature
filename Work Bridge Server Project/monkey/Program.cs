using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace monkey
{
    class Program
    {
        public static int _ConnectionID = 0;
        public static int port = 37666;
        public static Socket _socket;
        public static string serverName = "Work Bridge";
        
        //Connection ID, Client
        
        static void Main(string[] args)
        {
            Console.Title = serverName;
            Console.WriteLine("Starting server.");
            _socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                Console.WriteLine("Trying to listen on port " + port.ToString());
                _socket.Bind(new IPEndPoint(IPAddress.Any, port));
                _socket.Listen(1);
                _socket.BeginAccept(AcceptCallback, _socket);
            }
            catch
            {
                Console.WriteLine(string.Format("Could not start listening on port {0}, are you starting this program again?", port.ToString()));
                Thread.Sleep(5000);
                Environment.Exit(0);
            }
            Console.Title = serverName + " - Port " + port.ToString();
            Console.WriteLine("Server succesfully started.");
            Console.ReadLine();
        }

        public static void AcceptCallback(IAsyncResult ar)
        {
            var socket = (Socket)ar.AsyncState;

            try
            {
                var clientSocket = socket.EndAccept(ar);

                Interlocked.Increment(ref Global.Connected);
                Console.WriteLine(string.Format("New connection from {0}", clientSocket.RemoteEndPoint));

                var client = new Client();
                client.ConnectionID = Interlocked.Increment(ref _ConnectionID);
                client.socket = clientSocket;
                client.Stream = new NetworkStream(clientSocket);

                Global.Clients.Add(client.ConnectionID, client);

                client.Stream.BeginRead(client.ReceiveBuffer, 0, client.ReceiveBuffer.Length, ReadCallback, client.ConnectionID);
            }
            catch (Exception ex)
            {
                Console.WriteLine(string.Format("AcceptCallback err: {0}", ex.Message));
            }

            socket.BeginAccept(AcceptCallback, socket);
        }

        public static void ReadCallback(IAsyncResult ar)
        {
            var connectionID = (long)ar.AsyncState;
            Client client;

            lock (Global.Clients)
            {
                try
                {
                    client = Global.Clients[connectionID];
                }
                catch
                {
                    return;
                }

            }

            try
            {
                if (client.connected)
                {
                    int length = client.Stream.EndRead(ar);

                    if (length == 0)
                    {
                        client.CloseSocket();
                        return;
                    }

                    client.HandleBuffer(client.ReceiveBuffer, length);

                    client.Stream.BeginRead(client.ReceiveBuffer, 0, client.ReceiveBuffer.Length, ReadCallback, client.ConnectionID);
                }
            }
            catch (System.IO.IOException)
            {
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                client.CloseSocket();
            }
        }
    }
}