using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace monkey
{
    public static class PacketHandler
    {
        public static void HandlePacket(Client client, JObject data)
        {
            string cmd = data.Value<string>("cmd");
            if (cmd == "login")
            {
                string email = data.Value<string>("mail");
                string pass = data.Value<string>("pass");

                User usr = Helpers.getClient(email, pass);

                if (usr != null)
                {
                    client.user = usr;

                    client.Send(new
                    {
                        cmd = "login",
                        success = true,
                        mail = usr.email,
                        city = usr.city,
                        name = usr.name,
                        surname = usr.surname,
                        isWorker = usr.isWorker,
                        interests = usr.interests,
                        about = usr.about

                    });
                }
                else
                {
                    client.Send(new
                    {
                        cmd = "login",
                        success = false
                    });
                }
            }
            else if (cmd == "register")
            {
                string email = data.Value<string>("mail");
                string pass = data.Value<string>("pass");
                string name = data.Value<string>("name");
                string surname = data.Value<string>("surname");
                string city = data.Value<string>("city");
                bool isWorker = data.Value<bool>("isWorker");

                User newUser = new User()
                {
                    email = email,
                    name = name,
                    surname = surname,
                    password = pass,
                    city = city,
                    isWorker = isWorker,
                    about = "",
                    interests = ""
                };


                if (Helpers.addClient(newUser))
                {
                    client.user = newUser;
                    client.Send(new
                    {
                        cmd = "register",
                        mail = newUser.email,
                        name = newUser.name,
                        surname = newUser.surname,
                        city = newUser.city,
                        isWorker = newUser.isWorker,
                        success = true,
                        about = "",
                        interests = ""

                    });
                }
                else
                {
                    client.Send(new
                    {
                        cmd = "register",
                        success = false
                    });
                }  
            }
            else if (cmd == "finishReg")
            {
                string about = data.Value<string>("info");
                string interests = data.Value<string>("interests");

                client.user.about = about;
                client.user.interests = interests;

                client.Send(new
                {
                    cmd = "finishReg",
                    success = true,
                    about = about,
                    interests = interests
                });
            }
            else if (cmd == "saveUserData")
            {
                string name = data.Value<string>("name");
                string surname = data.Value<string>("surname");
                string email = data.Value<string>("mail");
                string city = data.Value<string>("city");
                string about = data.Value<string>("about");
                string interests = data.Value<string>("interests");
                bool isWorker = data.Value<bool>("isWorker");

                client.user.name = name;
                client.user.surname = surname;
                client.user.email = email;
                client.user.city = city;
                client.user.about = about;
                client.user.interests = interests;
                client.user.isWorker = isWorker;

                client.Send(new
                {
                    cmd = "saveUserData",
                    mail = email,
                    name = name,
                    surname = surname,
                    city = city,
                    isWorker = isWorker,
                    success = true,
                    about = about,
                    interests = interests
                });
            }
            else if(cmd == "getJobOffer")
            {

                bool jobFound = false;
                Helpers.UpdateJobOffers();
                int i;
                for(i = client.nextViewingJobOffer;i<Global.jobOffers.Count;i++)
                {
                    client.nextViewingJobOffer = i+1; //+1 because in the next time we don't want to offer the same job
                    JobOffer offer = Global.jobOffers[i];

                    for(int j = 0;j < offer.tags.Count;j++)
                    {
                        if(string.IsNullOrEmpty(client.user.interests) || client.user.interests.ToLower().Contains(offer.tags[j].ToLower()))
                        {
                            if(!client.ViewedJobOffers.Contains(i))
                            {
                                client.Send(new
                                {
                                    cmd = "getJobOffer",
                                    success = true,
                                    base64Image = offer.base64Image,
                                    description = offer.description,
                                    location = offer.location,
                                    pay = offer.pay
                                });
                                client.ViewedJobOffers.Add(i);
                                //Offer this job
                                jobFound = true;
                                break;
                            }
                        }
                    }
                    if (jobFound) break;
                }
                client.nextViewingJobOffer = 0;
                //No job offers available
                if(!jobFound)
                {
                    client.Send(new
                    {
                        cmd = "getJobOffer",
                        success = false,
                    });
                }
                
            }
        }
    }
}
