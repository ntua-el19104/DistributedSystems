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
but he never receives it. 
- [ ] It must be forbidden to send a transaction when the network isn't finished yet. Is it ? 
If not, implement this restriction.
- [ ] Divide the bootstrap code from the regular nodes' code.
- [ ] Should the broadcast of the transactions with the initial amount of each node happen 
before the broadcast of the first block of the blockchain ? Ensure that the wanted result 
occurs every time.
