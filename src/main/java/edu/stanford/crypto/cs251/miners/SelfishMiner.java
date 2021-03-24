package cs441641.miners;

import cs441641.blockchain.Block;
import cs441641.blockchain.NetworkStatistics;

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
        //double connectivity = (double) this.getConnectivity() / this.curNetStats.getTotalConnectivity();
        double hash_percent = (double) this.getHashRate() / this.curNetStats.getTotalHashRate();
        // int TO_ATTACK = 10;
        int TO_ATTACK = 5;
        int lead =  this.privateHead.getHeight() - this.publicHead.getHeight();
        if(lead > TO_ATTACK){
            // make the publicHead
            this.publicHead = this.privateHead;
            int adv = hash_percent > .37 ? 0 : 1;
            // .30 is arbitrary but it seems to work well for maintaining consistency
            while(lead > adv && this.publicHead.getPreviousBlock() != null){
                // announce a new head that is in front of theirs
                this.publicHead = this.publicHead.getPreviousBlock();
                lead--;
            }
        }
        
        return this.publicHead;
    }

    @Override
    public void blockMined(Block newBlock, boolean isMinerMe) {
        double hash_percent = (double) this.getHashRate() / this.curNetStats.getTotalHashRate();
        // int adv = connectivity > .30 ? 2 : 1;
        int adv = hash_percent > .37 ? 0 : 1;
        if(isMinerMe == true) {
            //if i mined this block
            if (newBlock.getHeight() > this.privateHead.getHeight()) {
                // if its a new block
                // it now becomes what I am extending
                this.privateHead = newBlock;
                // but i dont publish it
            }
        } else {
            if (newBlock != null && newBlock.getHeight() >= this.publicHead.getHeight()){
                // if someone broadcast a new block that is newer than my public head
                if (newBlock.getHeight() > this.privateHead.getHeight()){
                    // if its also better than my private one, adopt it
                    this.privateHead = newBlock;
                    this.publicHead = newBlock;
                } else if (hash_percent > .37 || newBlock.getHeight() + adv >= this.privateHead.getHeight()){
                // } else {
                    int lead =  this.privateHead.getHeight() - newBlock.getHeight();
                    // make the publicHead
                    this.publicHead = this.privateHead;
                    // int adv = connectivity > .30 ? 0 : 1;
                    // .30 is arbitrary but it seems to work well for maintaining consistency
                    while(lead > adv && this.publicHead.getPreviousBlock() != null){
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
