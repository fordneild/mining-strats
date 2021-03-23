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
            if (newBlock.getHeight() > this.privateHead.getHeight()) {
                this.privateHead = newBlock;
            }
        }else{
            if(newBlock != null && newBlock.getHeight() >= this.publicHead.getHeight()){
                if(newBlock.getHeight() > this.privateHead.getHeight()){
                    this.privateHead = newBlock;
                    this.publicHead = newBlock;
                }else{
                    int lead = newBlock.getHeight() - this.privateHead.getHeight();
                    this.publicHead = this.privateHead;
                    while(lead > 0 && this.publicHead.getPreviousBlock() != null){
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