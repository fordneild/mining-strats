package cs441641.miners;
import java.util.ArrayList;
import java.util.List;

import cs441641.blockchain.Block;
import cs441641.blockchain.NetworkStatistics;

public class FeeSnipingMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private List<Block> myOldBlocks;

    public FeeSnipingMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
        myOldBlocks = new ArrayList<Block>();

    }

    @Override
    public Block currentlyMiningAt() {
        if(this.myOldBlocks.size() > 0){
            return this.myOldBlocks.get(this.myOldBlocks.size()-1);
        }
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }



    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if(isMinerMe) {
            myOldBlocks.add(block);
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
            }
        }else{
            if (currentHead == null) {
                currentHead = block;
            } else if (block != null && block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
    
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
