### TODO List

- [ ] Add Java code format and apply it to project
- [ ] Add maven dependencies for testing 
- [ ] Create tests for blockchainService
- [ ] Create RabbitMqCommunication implementation
- [ ] Find ways to test RabbitMqCommunication (with mocked blockainService)


### COMMUNICATION THROUGH RABBITMQ TODOS
- [ ] Add a timeout in the node's waiting for the id
- [ ] Handle the scenario where a node disconnects and tries to reconnect
- [ ] Handle the case where a node tries to connect, bootstrap sends him his id,
but he never receives it. Also, the last node may take the addresses message before 
he takes hid id. Is that a problem ?
