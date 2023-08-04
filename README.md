# customers-api

I designed the application as two modules for the open api, and the other one is the actual api implementation.
# Assumptions:
. Customer id field is automatically generated as UUID Field, and the corresponding entities are treated in the same way as Address and son.
. I tried to add some addition functionalities which I thought it could be useful 
. There are some validations implemented in addition to JSON Schema validation, like the email and phone numbers should be unique per customer. Also, handling the case of customer not found

# Future work
. Invest more on testing Junit and integration and the time was limited somehow.
. Definitely refactoring on different parts.
. Enhancing the performance specially for bulk operations
- Docker and Kubernetes
- Provide a detailed documentation, as the mentioned comments on my code are not sufficient for documentation but just to clarify the main parts.

#How to run it
Just mvn clean install, and the application should be up on port 9001. I have used swagger UI to facilitate testing and usage in general.


