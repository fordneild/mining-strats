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
        if(this.withheldBlock != null){
            return this.withheldBlock;
        }
        return this.currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        Block curBlock;
        int curNumWithHeld;
        if(isMinerMe == true) {
            numWithheld++;
            this.withheldBlock = block;
        }
        if(isMinerMe == false && block != null){
            if(this.withheldBlock == null || this.withheldBlock.getHeight() < block.getHeight()){
                this.currentHead = block;
                this.withheldBlock = null;
                this.numWithheld = 0;
            }else{
                curBlock  = this.withheldBlock;
                curNumWithHeld = this.numWithheld;
                while(curNumWithHeld > 0 && curBlock.getPreviousBlock() != null){
                    if(curBlock.getHeight() - 1 <= block.getHeight()){
                        break;
                    }
                    curBlock = curBlock.getPreviousBlock();
                    curNumWithHeld--;
                }
                this.currentHead = curBlock;
                this.numWithheld = this.numWithheld - curNumWithHeld;
                if(numWithheld == 0){
                    this.withheldBlock = null;
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
