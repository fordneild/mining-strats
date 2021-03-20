package edu.stanford.crypto.cs251.miners;

import edu.stanford.crypto.cs251.blockchain.Block;
import edu.stanford.crypto.cs251.blockchain.NetworkStatistics;

public class MajorityMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private double hashPercent;

    public MajorityMiner(String id, int hashRate, int connectivity) {
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
        }else{
            if(this.hashPercent > .5){
                this.blockMinedMajority(block);
            }else{
                this.blockMinedNormal(block);
            }
        }
    }

    private void blockMinedNormal(Block block) {
        if (currentHead == null) {
            currentHead = block;
        } else if (block != null && block.getHeight() > currentHead.getHeight()) {
            this.currentHead = block;

        }
    }

    private void blockMinedMajority(Block block) {
        // 1 => very agressive
        // 100 => not so agressive
        int BASE_DISTANCE_WE_CAN_WIN_FROM = 8;
        int AGRESSIVENESS_DAMPNER = 7;
        int hashPercentAgressivenessBonus = (int) Math.round(((this.hashPercent*100) - 50)/AGRESSIVENESS_DAMPNER);
        int DISTANCE_WE_CAN_WIN_FROM = BASE_DISTANCE_WE_CAN_WIN_FROM + hashPercentAgressivenessBonus;
        if (currentHead == null) {
            //out chain is empty
            currentHead = block;
        } else if (block != null && block.getHeight() > currentHead.getHeight()+DISTANCE_WE_CAN_WIN_FROM) {
            // they are ahead of us by alot, lets give up and join thier chain
            Block curBlock = block;
            for(int i =0; i<DISTANCE_WE_CAN_WIN_FROM; i++){
                curBlock = block.getPreviousBlock();
            }
            this.currentHead = curBlock;
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        updateIsMajority(networkStatistics);

    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        updateIsMajority(statistics);
    }

    private void updateIsMajority(NetworkStatistics statistics) {
        this.hashPercent = (this.getHashRate()*1.0)/ statistics.getTotalHashRate();
    }
}
