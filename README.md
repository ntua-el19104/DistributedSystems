# Distributed Systems Project
### NTUA ECE 9th semester 2023- 2024

#### Contributors:
* **Theodora Boutsini** 03119094
* **Georgios Babanis** 03119083
* **Evangelos Kontiannis** 01319104

### [Project Description](./DistributedProject2024.pdf)

### Project Implementation
#### [Node](src/main/java/gr/ntua/blockchainService/Node.java)
Το σύστημα μας αποτελείται από πανομοιότυπους κόμβους , εκτός του bootstrap που έχει κάποια επιπλεόν λειτουργικότητα μόνο κατά
την εκκίνηση και έπειτα συμπεριφέρεται ακριβώς όπως όλοι οι υπόλοιποι. 
Κάθε κόμβος έχει το wallet του, το Blockchain, το nonce του(αύξων αριθμών των transaction που
έχει δημιουργήσει ο κόμβος για να αποφύγουμε replay attacks), μια λίστα NodeInfo με την 
κατάσταση του δικτύου και ένα instance της communication κλάσης ώστε να επικοινωνεί με τους υπόλοιπους κόμβους.



#### [Wallet](src/main/java/gr/ntua/blockchainService/Wallet.java)
Περιέχει το private και το public key του κόμβου και έχει την δυνατότητα να transcations.

#### [Transaction](src/main/java/gr/ntua/blockchainService/Wallet.java)
Κάθε transaction περιέχει το πόσο και το μήνυμα που μεταφέρεται, τα public keys του αποστολέα 
και παραλήπτη, το μέγεθος του fee που χρεώνει ο validator, το hash και την υπογραφή του αποστολέα.

#### Blockchain
Μια λίστα από blocks που περιλαμβάνει όλες τις καταγεγραμμένες έγκυρες συναλλαγές.

#### [Block](src/main/java/gr/ntua/blockchainService/Block.java)
Περιέχει ένα index με τη θέση του στο blockchain, το hash του, το hash του προηγούμενου block, το id του validator, την ώρα
έκδοσης του block και τη λίστα με τα transactions που περιέχει.

#### Εξωτερικές βιβλιοθήκες
Για την δημιουργία hash



### Deployment
