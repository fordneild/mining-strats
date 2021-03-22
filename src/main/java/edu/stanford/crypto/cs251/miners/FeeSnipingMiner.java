package edu.stanford.crypto.cs251.miners;

import edu.stanford.crypto.cs251.blockchain.Block;
import edu.stanford.crypto.cs251.blockchain.NetworkStatistics;

public class FeeSnipingMiner extends BaseMiner {
    private Block currentHead;

    public FeeSnipingMiner(String id, int hashRate, int connectivity) {
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
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
            }
        }
        else{
            if(block != null){
                if (currentHead == null) {
                    currentHead = block;
                }else if (block.getHeight() == currentHead.getHeight() + 1 && this.isBlockProfitable(block)){
                    // reject it temorarily
                }else if (block.getHeight() > currentHead.getHeight()) {
                    // adopt it if its unintresting or too far ahead for us to catch up
                    this.currentHead = block;
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

    private boolean isBlockProfitable(Block block){
        if(block.getBlockValue() > 6){
            return true;
        }
        return false;
    }
}
