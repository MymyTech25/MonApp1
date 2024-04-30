The registration activity allows you to enter the user's email address, password, mobile phone number and role (buyer, planner or driver). 
If he is a driver, he must also enter the registration number of his truck. 

Once the user has been authenticated, he has the possibility to change the following information: email address, password, mobile phone number, 
and if applicable, the registration number of his truck. 

As a customer, the user can select from a list of products stored hard in Firebase, the products he needs as well as the quantity in Kg. 
He then chooses the date and delivery address. The latter can be entered from a form or can be selected directly from the OSM card. 

As a planner, the user has access to the list of all deliveries to be made (sort by date). 
It can assign a route to a driver (An itinerary is a set of destination points). 
A notification is immediately sent to the driver. He can then either accept or refuse the mission. 

As a driver, from the "Waiting" activity, the user can accept or refuse a pending mission. 
The user can consult from the "In progress" activity, the list of missions he has accepted. 
By clicking on a mission, the route, with the different stages, is displayed on an OpenStreetMap map. 
For each stage, the distance in Km and the duration of the trip are retrieved from the Time- Distance Matrix1 web service and displayed. 
The user can consult from the "History" activity, the list of missions he has already completed.
