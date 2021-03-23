package edu.stanford.crypto.cs251.miners;

import edu.stanford.crypto.cs251.blockchain.Block;
import edu.stanford.crypto.cs251.blockchain.NetworkStatistics;

public class SelfishMiner extends BaseMiner {
    private Block publicHead;
    private Block privateHead;
    private NetworkStatistics curNetStats;

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
        } else {
            if (newBlock != null && newBlock.getHeight() > this.publicHead.getHeight()){
                // if someone broadcast a new block that is newer than my public head
                if (newBlock.getHeight() > this.privateHead.getHeight()){
                    // if its also better than my private one, adopt it
                    this.privateHead = newBlock;
                    this.publicHead = newBlock;
                } else {
                    int lead =  this.privateHead.getHeight() - newBlock.getHeight();
                    double connectivity = (double) this.getConnectivity() / this.curNetStats.getTotalConnectivity();
                    // make the publicHead
                    this.publicHead = this.privateHead;
                    //.30 is arbitrary but it seems to work well for maintaining consistency
                    while(lead > 0 && this.publicHead.getPreviousBlock() != null && connectivity > .30){
                        // announce a new head that is in front of theirs
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
        this.curNetStats = networkStatistics;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        this.curNetStats = statistics;
    }

    private int getNumWithheld(){
        return this.privateHead.getHeight() - this.publicHead.getHeight();
    }
}
