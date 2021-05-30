using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace monkey
{
    public static class Global
    {
        public static Dictionary<long, Client> Clients = new Dictionary<long, Client>();
        public static List<JobOffer> jobOffers = new List<JobOffer>();

        public static int Connected = 0;
        public static string usersFileName = "Users.json";
        public static string jobOffersFileName = "JobOffers.json";
    }
}
