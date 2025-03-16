package cia.northboat.lc;

public class Back {

    // 快速幂，用乘法手动实现幂运算
    public static double myPow(double x, int n) {
        long N = n;
        boolean flag = N < 0;
        N = N < 0 ? -N : N;
        double res = 1, mul = x;
        while(N > 0){
            if((N & 1) == 1){
                res *= mul;
            }
            mul *= mul;
            N >>= 1;
        }
        return flag ? (1.0 / res) : res;
    }

    // 用加法计算 ax
    public static int myMul(int a, int x){
        boolean flag = (a > 0 && x < 0) || (a < 0 && x > 0);
        a = Math.abs(a); x = Math.abs(x);
        int add = a, ans = 0;
        while(x != 0){
            if((x & 1) == 1){
                ans += add;
            }
            if(x != 1){
                add += add;
            }
            // 用掉当前位，考虑更高位
            x >>= 1;
        }
        return flag ? -ans : ans;
    }

    // 司马了
    public static int divide(int dividend, int divisor) {
        if(dividend == Integer.MIN_VALUE){
            if(divisor == 1)    return Integer.MIN_VALUE;
            if(divisor == -1)   return Integer.MAX_VALUE;
        }
        if(divisor == Integer.MIN_VALUE){
            return dividend == Integer.MIN_VALUE ? 1 : 0;
        }

        // 判别答案正负（若 flag 则答案为负），并全部置正
        boolean flag = (dividend > 0 && divisor < 0) || (dividend < 0 && divisor > 0);
        dividend = dividend > 0 ? -dividend : dividend;
        divisor = divisor > 0 ? -divisor : divisor;

        // 二分查找
        int left = 1, right = Integer.MAX_VALUE, res = 0;
        while(left <= right){
            int mid = left + ((right-left) >> 1);
            System.out.println(mid);
            if(quickAdd(divisor, mid, dividend)){
                res = mid;
                System.out.println("你妈死了: " + res + "\n");
                if(mid == Integer.MAX_VALUE)    break;
                left = mid + 1;
            } else {
                System.out.println("我操死你的吗: " + res);
                right = mid - 1;
            }
        }
        return flag ? -res : res;
    }

    // 用加法计算 x*y
    public static int myQuickAdd(int a, int x, int y){
        int add = a, ans = 0;
        while(x != 0){
            if((x & 1) == 1){
                // 若当前位已经有 res+add > z，说明 x*y>z 肯定成立
                if(ans + add > y){
                    return ans;
                }
                ans += add;
            }
            if(x != 1){
                // 判断 y 是否用尽，若没用尽，则肯定能继续循环
                // 那么下一轮中，若加数都大于了总数 z，那么一定有 x * y > z
                // 所以这里如果成立可以直接返回 false
                if(add + add > y){
                    return ans;
                }
                add += add;
            }
            System.out.println(ans);
            // 用掉当前位，考虑更高位
            x >>= 1;
        }
        // 说明 y 每位用尽了仍不大于 z，此时有 x * y <= z，返回 true
        return ans;
    }

    // 计算 a*x <= y
    public static boolean quickAdd(int a, int x, int y) {
        // x 和 y 是负数，z 是正数
        // 需要判断 z * y >= x 是否成立
        int result = 0, add = a;
        while (x != 0) {
            if ((x & 1) != 0) {
                // 需要保证 result + add >= x
                if (result < y - add) {
                    return false;
                }
                result += add;
            }
            if (x != 1) {
                // 需要保证 add + add >= x
                if (add < y - add) {
                    return false;
                }
                add += add;
            }
            System.out.println(result);
            // 不能使用除法
            x >>= 1;
        }
        return true;
    }

    public static int findDuplicate(int[] nums) {
        int n = nums.length, res = -1;
        int left = 1, right = n-1;
        while(left <= right){
            int mid = left + ((right-left) >> 1);
            int count = 0;
            for (int num : nums) {
                if (num <= mid) {
                    count++;
                }
            }
            if(count <= mid){
                left = mid+1;
            } else {
                right = mid-1;
                res = mid;
            }
        }
        return res;
    }
}
