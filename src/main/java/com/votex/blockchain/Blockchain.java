package com.votex.blockchain;

import com.votex.crypto.HashUtil;
import com.votex.model.Vote;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class Blockchain {
    private List<Block> chain;
    private int difficulty;

    public Blockchain() {
        this.chain = new ArrayList<>();
        this.difficulty = 4;  // Initial mining difficulty
        createGenesisBlock();
    }

    public Blockchain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
        createGenesisBlock();
    }

    private void createGenesisBlock() {
        Block genesisBlock = new Block();
        genesisBlock.setIndex(0);
        genesisBlock.setPreviousHash("0");
        genesisBlock.setMerkleRoot("");
        genesisBlock.setNonce(0);
        genesisBlock.setHash(genesisBlock.calculateHash());

        chain.add(genesisBlock);
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addBlock(Block newBlock) {
        Block latestBlock = getLatestBlock();
        newBlock.setIndex(latestBlock.getIndex() + 1);
        newBlock.setPreviousHash(latestBlock.getHash());
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
    }

    public void addBlock(List<String> transactions) {
        Block newBlock = new Block();
        newBlock.setTransactions(transactions);
        addBlock(newBlock);
    }

    public void mineBlock(Block block) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!block.getHash().substring(0, difficulty).equals(target)) {
            int nonce = block.getNonce() + 1;
            block.setNonce(nonce);
            block.setHash(block.calculateHash());
        }
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Verify current block's hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                return false;
            }

            // Verify link to previous block
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
            
            // Verify merkleRoot matches transactions (this catches transaction tampering)
            String calculatedMerkleRoot = HashUtil.getMerkleRoot(currentBlock.getTransactions());
            if (!calculatedMerkleRoot.equals(currentBlock.getMerkleRoot())) {
                return false;
            }
        }
        return true;
    }

    public List<Block> getChain() {
        return chain;
    }

    public void setChain(List<Block> chain) {
        this.chain = chain;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}