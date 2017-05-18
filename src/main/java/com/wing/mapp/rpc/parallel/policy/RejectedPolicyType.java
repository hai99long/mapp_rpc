package com.wing.mapp.rpc.parallel.policy;

/**
 * Created by wanghl on 2017/5/3.
 */
public enum RejectedPolicyType {
    ABORT_POLICY("AbortPolicy"),
    BLOCKING_POLICY("BlockingPolicy"),
    CALLER_RUNS_POLICY("CallerRunsPolicy"),
    DISCARDED_POLICY("DiscardedPolicy"),
    DISCARDED_OLDEST_POLICY("DiscardOldestPolicy");

    private String value;

    private RejectedPolicyType(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
    public static RejectedPolicyType getRejectedPolicyType(String value){
        for(RejectedPolicyType type:RejectedPolicyType.values()){
            if(type.getValue().equalsIgnoreCase(value.trim())){
                return type;
            }
        }
        throw new IllegalArgumentException("Mismatched type with value=" + value);
    }
    public String toString() {
        return value;
    }
}
