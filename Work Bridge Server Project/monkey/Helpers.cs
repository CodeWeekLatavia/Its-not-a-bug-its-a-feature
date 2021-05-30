using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace monkey
{
    public static class Helpers
    {

        public static User getClient(string email, string pass)
        {
            List<User> currentClients = getClients();

            for(int i =0;i<currentClients.Count;i++)
            {
                if(currentClients[i].email == email && currentClients[i].password == pass)
                {
                    return currentClients[i];
                }
            }
            return null;
        }

        public static void updateClient(User client)
        {
            List<User> currentClients = getClients();

            bool u = false;
            for (int i = 0; i < currentClients.Count; i++)
            {
                if (currentClients[i].email == client.email)
                {
                    currentClients[i] = client;
                    u = true;
                    break;
                }
            }
            if (u) saveClients(currentClients);


        }

        public static List<User> getClients()
        {
            if(File.Exists(Global.usersFileName))
            {
                string content = File.ReadAllText(Global.usersFileName);
                List<User> clients = JsonConvert.DeserializeObject<List<User>>(content);
                return clients;
            }
            return new List<User>();

        }

        public static void saveClients(List<User> clients)
        {
            string json = JsonConvert.SerializeObject(clients);
            File.WriteAllText(Global.usersFileName, json);
        }

        public static bool addClient(User client)
        {
            List<User> currentClients = getClients();

            bool exists = false;

            for(int i =0;i<currentClients.Count;i++)
            {
                if(client.email == currentClients[i].email)
                {
                    exists = true;
                    break;
                }
            }

            if (!exists)
            {
                currentClients.Add(client);
                saveClients(currentClients);
                return true;
            }
            return false;
        }

        public static void UpdateJobOffers()
        {
            if(File.Exists(Global.jobOffersFileName))
            {
                try
                {
                    string content = File.ReadAllText(Global.jobOffersFileName);
                    Global.jobOffers = JsonConvert.DeserializeObject<List<JobOffer>>(content);
                }
                catch (Exception)
                {
                    Console.WriteLine("Job offers file is probably corrupted");
                }
                
            }
        }
    }
}
