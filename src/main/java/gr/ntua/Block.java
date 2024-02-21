package gr.ntua;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;

public class Block {
    private final int CAPACITY = 10;
    private int index;
    private LocalDateTime timestamp;
    private PublicKey validator;
    private String currentHash;
    private String previousHash;
    private List<Transaction> transactionList;


}