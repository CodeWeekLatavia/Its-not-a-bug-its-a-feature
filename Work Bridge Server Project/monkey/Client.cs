using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace monkey
{
    public class Client
    {
        public long ConnectionID { get; set; }

        public Socket socket { get; set; }

        public NetworkStream Stream { get; set; }

        public byte[] ReceiveBuffer { get; set; }

        public User user { get; set; }

        public ulong UserID { get; set; }

        public bool connected { get; set; }

        public bool loggedIn { get; set; }

        public int nextViewingJobOffer = 0;

        public List<int> ViewedJobOffers = new List<int>();

        public Client()
        {
            connected = true;
            loggedIn = false;
            ReceiveBuffer = new byte[4096];
        }

        List<byte> packet = new List<byte>();
        public void HandleBuffer(byte[] buffer, int length)
        {
            //Console.WriteLine(string.Format("Handling buffer (Length {0}): {1}", length, BitConverter.ToString(buffer, 0, length)));
            for (int i = 0; i < length; i++)
            {
                if (buffer[i] == 0x0a) //end of packet
                {
                    HandlePacket(packet.GetRange(0,packet.Count).ToArray());
                    packet.Clear();
                    continue;
                }
                packet.Add(buffer[i]);
            }
        }

        public void HandlePacket(byte[] packet)
        {
            //packet bytes does not have 0x0a included

            string json = Encoding.UTF8.GetString(packet);
            JObject jsonObject = JObject.Parse(json);
            
            Console.WriteLine("CLIENT -> SERVER");

            string[] lines = new string[] { "{", "}" };

            if (!string.IsNullOrEmpty(json)) lines = JsonConvert.SerializeObject(JsonConvert.DeserializeObject(json), Formatting.Indented).Split(new[] { Environment.NewLine }, StringSplitOptions.None);

            foreach (string line in lines)
            {
                Console.WriteLine(line);
            }
            Console.WriteLine();

            PacketHandler.HandlePacket(this, jsonObject);

        }

        public void CloseSocket()
        {
            lock (Global.Clients)
            {
                if (Global.Clients.ContainsKey(ConnectionID))
                {
                    Global.Clients.Remove(ConnectionID);
                }
            }

            if (connected && socket != null)
            {
                if (user != null && user.email != null) Helpers.updateClient(user);
                if (user != null && user.name != null) Console.WriteLine(string.Format("{0} disconnected", user.name));
                else Console.WriteLine(string.Format("{0} disconnected", socket.RemoteEndPoint));
                connected = false;
                Interlocked.Decrement(ref Global.Connected);

                try
                {
                    try
                    {
                        socket.Shutdown(SocketShutdown.Both);
                    }
                    catch { }
                    socket.Close();

                }
                catch
                {

                }

            }

        }

        public void Send(object data)
        {
            List<byte> response = new List<byte>();
            response.AddRange(Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(data)));
            response.Add(0x0a);//End of packet

            Console.WriteLine("SERVER -> CLIENT");
            string[] lines = new string[] { "{", "}" };
            if (data != null) lines = JsonConvert.SerializeObject(data, Formatting.Indented).Split(new[] { Environment.NewLine }, StringSplitOptions.None);
            foreach (string line in lines)
            {
                if (!line.Contains("base64Image")) Console.WriteLine(line);
            }
            Console.WriteLine(Environment.NewLine);
            
            byte[] packet = response.ToArray();

            lock (Stream)
            {
                try
                {
                    Stream.Write(packet, 0, packet.Length);
                    Stream.Flush();
                }
                catch
                {

                    CloseSocket();
                }

            }

        }


    }
}
