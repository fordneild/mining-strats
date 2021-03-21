package edu.stanford.crypto.cs251.miners;

import edu.stanford.crypto.cs251.blockchain.Block;
import edu.stanford.crypto.cs251.blockchain.NetworkStatistics;

public class SelfishMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private Block withheldBlock;
    private int numWithheld;

    public SelfishMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if(isMinerMe) {
            numWithheld++;
            this.withheldBlock = block;
            // maybe just take advantage of our lead
            if(numWithheld > 1){
                this.currentHead = this.withheldBlock;
                this.withheldBlock = null;
                this.numWithheld = 0;
            }
        }
        else{
            if(block != null){
                int withheldHeight = this.numWithheld + this.currentHead.getHeight();
                if(this.withheldBlock == null || withheldHeight < block.getHeight()){
                    //give up and switch to thier block
                    this.currentHead = block;
                }else{
                    this.currentHead = this.withheldBlock;
                    this.withheldBlock = null;
                    this.numWithheld = 0;
                }
            }
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {

    }
}
