# fraud-detection
fraud detection application using apache kafka, springboot microservices, etc.
First, we'll have a message as a json object, containing all the user details, such as account name, account number, pincode, IP address, transaction amount, mode of payment, bank name, kind of network: cellular or wifi, etc., birthdate of account holder, home network availability, kind of application through which payment was done: 3rd party application, upi, netbanking, etc.
It can be uploaded via an excel, with each row containing transaction details containing above details
Then its converted to a json object, and sent to a kafka queue.
Then, we should implement microservices and the json objects should be displayed using GET, and it's argument should be transaction amount validation values
We should have a rule engine, containing a set of rules, like "if Age>60", "amount>50000", etc.
The transaction details should be validated with the rules from the rule engine, and then the details of the violated rules should be displayed using a UI
We should pick one transaction id validate against all rules, then next, like that, All in one-go
