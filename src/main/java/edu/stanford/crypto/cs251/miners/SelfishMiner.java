package edu.stanford.crypto.cs251.miners;

import edu.stanford.crypto.cs251.blockchain.Block;
import edu.stanford.crypto.cs251.blockchain.NetworkStatistics;

public class SelfishMiner extends BaseMiner {
    private Block publicHead;
    private Block privateHead;

    public SelfishMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentlyMiningAt() {
        return this.privateHead;
    }

    @Override
    public Block currentHead() {
        return this.publicHead;
    }

    @Override
    public void blockMined(Block newBlock, boolean isMinerMe) {
        if(isMinerMe == true) {
            //if i mined this block
            if (newBlock.getHeight() > this.privateHead.getHeight()) {
                // if its a new block
                // it now becomes what I am extending
                this.privateHead = newBlock;
                // but i dont publish it
            }
        }else{
            if(newBlock != null && newBlock.getHeight() > this.publicHead.getHeight()){
                // if someone broadcasted a new block that is newer than my pubilic head
                if(newBlock.getHeight() > this.privateHead.getHeight()){
                    // if its also better than my private one, adpot it
                    this.privateHead = newBlock;
                    this.publicHead = newBlock;
                }else{
                    int lead =  this.privateHead.getHeight() - newBlock.getHeight();
                    // make the publicHead
                    this.publicHead = this.privateHead;
                    while(lead > 0 && this.publicHead.getPreviousBlock() != null){
                        // announce a new head that is in front of their
                        this.publicHead = this.publicHead.getPreviousBlock();
                        lead--;
                    }
                }
            }
        }
}


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.publicHead = genesis;
        this.privateHead = genesis;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {

    }

    private int getNumWithheld(){
        return this.privateHead.getHeight() - this.publicHead.getHeight();
    }
}
