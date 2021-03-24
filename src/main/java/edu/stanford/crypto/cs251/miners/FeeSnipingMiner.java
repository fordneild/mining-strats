package cs441641.miners;

import cs441641.blockchain.Block;
import cs441641.blockchain.NetworkStatistics;

public class FeeSnipingMiner extends BaseMiner {
    private Block currentHead;
    private NetworkStatistics curNetStats;

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
                } else if (block.getHeight() == currentHead.getHeight() + 1 && this.isBlockProfitable(block)){
                    // reject it temporarily
                } else if (block.getHeight() > currentHead.getHeight()) {
                    // adopt it if its uninteresting or too far ahead for us to catch up
                    this.currentHead = block;
                }
            }
        }
    }



    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        this.curNetStats = networkStatistics;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        this.curNetStats = statistics;
    }

    private boolean isBlockProfitable(Block block){
         double myPercent  = ((double) this.getHashRate() / curNetStats.getTotalHashRate());
         int limit;
        //  these are hard coded values discovered through testing
         if(myPercent >= .27){
            limit = 25;
         }else{
             limit = 50;
         }
        if(block.getBlockValue() > limit){
            return true;
        }
        return false;
    }
}
