package cia.northboat.lc;

import java.util.*;

public class Test {


    public class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    // 最长回文子串：动态规划
    public static String longestSubPalindrome(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        String res = s.substring(0, 1);
        for(int i = 0; i < n; i++){
            dp[i][i] = true;
            if(i < n-1 && s.charAt(i) == s.charAt(i+1)){
                dp[i][i+1] = true;
                res = s.substring(i, i+2);
            }
        }
        for(int k = 3; k <= n; k++){
            for(int i = 0; i <= n-k; i++){
                if(dp[i+1][i+k-2] && s.charAt(i) == s.charAt(i+k-1)){
                    dp[i][i+k-1] = true;
                    if(k > res.length()){
                        res = s.substring(i, i+k);
                    }
                }
            }
        }
        return res;
    }

    public static void reverseWords(char[] s) {
        int n = s.length;
        reverse(s, 0, n);
        for(int i = 0; i < n; i++){
            int j = i+1;
            while(j < n && s[j] != ' ')  j++;
            if(j == n){
                reverse(s, i, n);
                return;
            }
            reverse(s, i, j);
            i = j;
        }
    }

    public static void reverse(char[] s, int i, int j){
        int length = (j-i) >> 1;
        System.out.println(i + " " + j);
        System.out.println(length);
        for(int k = 0; k < length; k++){
            char temp = s[i+k];
            s[i+k] = s[j-k-1];
            s[j-k-1] = temp;
        }
        System.out.println(Arrays.toString(s));
    }

    public static boolean isIsomorphic(String s, String t) {
        return isomorphic(s, t) && isomorphic(t, s);
    }

    public static boolean isomorphic(String s, String t) {
        Map<Character, Character> map = new HashMap<>();
        int n = s.length();
        if(t.length() != n)
            return false;
        for(int i = 0; i < n; i++){
            char c1 = s.charAt(i), c2 = t.charAt(i);
            if(map.containsKey(c1) && map.get(c1) != c2){
                return false;
            } else {
                map.put(c1, c2);
            }
        }
        return true;
    }

    public static int rob(int[] nums) {
        int n = nums.length;
        int a = rob(nums, 0, n-1);
        int b = rob(nums, 1, n);
        return Math.max(a, b);
    }

    public static int rob(int[] nums, int i, int j){
        int n = j - i;
        int[] dp = new int[n+1];
        dp[0] = 0;
//        System.out.println(n);
        for(int k = 1; k < n+1; k++){
            if(k == 1){
                dp[1] = nums[i];
//                System.out.println("nmsl");
                continue;
            }
            int a = dp[k-2] + nums[i+k-1];
            int b = dp[k-1];
            dp[k] = Math.max(a, b);
//            System.out.println(k + " " + dp[k]);
        }
        return dp[n];
    }

    public static String simplifyPathRow(String path) {
        int n = path.length();
        int tail = 0, preTail = 0; // 记录最后两个斜杠的位置
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
//            System.out.println("===========");
//            System.out.println(sb);
            // 去掉重复的斜杠
            if(path.charAt(i) == '/'){
                if(i > 0 && path.charAt(i-1) == '/'){
                    continue;
                }
                sb.append('/');
                preTail = tail;
                tail = sb.length()-1;
            } else if (path.charAt(i) == '.'){
                int count = 0;
                while(i < n && path.charAt(i) == '.') {
                    count++; i++;
                }
//                System.out.println(count);
                if(count == 1 && sb.charAt(sb.length()-1) == '/' && (i == n || path.charAt(i) == '/')){
                    continue;
                } else if (count == 2 && sb.charAt(sb.length()-1) == '/' && (i == n || path.charAt(i) == '/')) {
                    if (tail == 0) {
                        continue;
                    }
                    sb.delete(preTail, sb.length());
                    sb.append('/');
                    tail = preTail;
                    for (int j = tail - 1; j >= 0; j--) {
                        if (sb.charAt(j) == '/') {
                            preTail = j;
                            break;
                        }
                    }
                    if(i < n && path.charAt(i) != '/'){
                        sb.append(path.charAt(i));
                    }
                } else {
                    while(count-- > 0)  sb.append('.');
                    if(i < n && path.charAt(i) == '/'){
                        sb.append('/');
                        preTail = tail;
                        tail = i;
                    } else if (i < n){
                        sb.append(path.charAt(i));
                    }
                }
            } else {
                sb.append(path.charAt(i));
            }
        }
        if(sb.length() > 1 && sb.charAt(sb.length()-1) == '/'){
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public static String simplifyPath(String path){
        String[] p = path.split("/");
        Deque<String> queue = new ArrayDeque<>();
        StringBuilder sb = new StringBuilder();
        for(String s: p){
//            System.out.println(s);
            if(s.equals("..")){
                if(!queue.isEmpty())
                    queue.pollLast();
            } else if (s.length() > 0 && !s.equals(".")){
                queue.offer(s);
            }
        }
        if(queue.isEmpty())     return "/";
        while(!queue.isEmpty()){
            sb.append("/");
            sb.append(queue.poll());
        }
        return sb.toString();
    }

    public static int maxLength(List<String> arr) {
        List<Integer> masks = new ArrayList<>();
        masks.add(0);
        int res = 0;
        for(String s: arr){
            int mask = 0;
            for(char c: s.toCharArray()){
                int ch = c - 'a';
                if(((mask >> ch) & 1) == 1){
                    mask = 0;
                    break;
                }
                mask |= (1 << ch);
            }
            if(mask == 0){
                continue;
            }
            int n = masks.size();
            for(int i = 0; i < n; i++){
                if((mask & masks.get(i)) == 0){
                    masks.add(masks.get(i) | mask);
                    res = Math.max(Integer.bitCount(masks.get(i) | mask), res);
                }
            }
        }
        return res;
    }

    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> res = new ArrayList<>();
        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = n-1, top = 0, bottom = m-1;
        while(left < right && top < bottom){
            for(int i = left; i < right; i++)   res.add(matrix[top][i]);
            for(int i = top; i < bottom; i++)   res.add(matrix[i][right]);
            for(int i = right; i > left; i--)   res.add(matrix[bottom][i]);
            for(int i = bottom; i > top; i--)   res.add(matrix[i][left]);
            left++; right--; top++; bottom--;
        }
        if(left > right || top > bottom)    return res;

        if(left < right){ while(left <= right){ res.add(matrix[top][left++]); }}
        else if (top < bottom){ while(top <= bottom){ res.add(matrix[top++][left]); }}
        else { res.add(matrix[top][left]); }
        return res;
    }

    public static String addStrings(String num1, String num2) {
        StringBuilder sb = new StringBuilder();
        int n1 = num1.length(), n2 = num2.length(), i;
        boolean flag = false;
        for(i = 0; i < Math.min(n1, n2); i++){
            int a = num1.charAt(n1-i-1) - '0';
            int b = num2.charAt(n2-i-1) - '0';
            int sum = a + b;
            if(flag)    sum += 1;
            flag = sum >= 10;
            sb.append(sum % 10);
        }
        String num = i < n1 ? num1 : num2;
        while(i < Math.max(n1, n2)){
            int c = num.charAt(Math.max(n1, n2)-i-1) - '0';
            if(flag)    c += 1;
            flag = c >= 10;
            sb.append(c % 10);
            i++;
        }
        if(flag)    sb.append(1);
        return sb.reverse().toString();
    }

    public static int clumsy(int n) {
        int count = 0, res = n--;
        Deque<Integer> ops = new ArrayDeque<>();
        Deque<Integer> nums = new ArrayDeque<>();
        while(n > 0){
            switch (count % 4) {
                case 0 -> {
                    if(!nums.isEmpty()){
                       int num = nums.pop();
                       nums.push(n*num);
                       break;
                    }
                    res *= n;
                }
                case 1 -> {
                    if(!nums.isEmpty()){
                        int num = nums.pop();
                        nums.push(num/n);
                        break;
                    }
                    res /= n;
                }
                case 2 -> { // 加法不能直接算
                    while(!ops.isEmpty()){
                        int op = ops.pop(), num = nums.pop();
                        if(op == 2){
                            res += num;
                        } else if(op == 3){
                            res -= num;
                        }
                    }
                    ops.push(2);
                    nums.push(n);
                }
                case 3 -> { // 减法同理
                    while(!ops.isEmpty()){
                        int op = ops.pop(), num = nums.pop();
                        if(op == 2){
                            res += num;
                        } else if(op == 3){
                            res -= num;
                        }
                    }
                    ops.push(3);
                    nums.push(n);
                }
            }
            n--; count++;
        }
        while(!ops.isEmpty()){
            int op = ops.pop(), num = nums.pop();
            if(op == 2){
                res += num;
            } else if(op == 3){
                res -= num;
            }
        }
        return res;
    }


    public static int largestValsFromLabels(int[] values, int[] labels, int numWanted, int useLimit) {
        List<int[]> pairs = new ArrayList<>();
        Map<Integer, Integer> limit = new HashMap<>();
        int n = values.length;
        for(int i = 0; i < n; i++){
            int[] pair = new int[2];
            pair[0] = values[i]; pair[1] = labels[i];
            pairs.add(pair);
            if(!limit.containsKey(labels[i])){
                limit.put(labels[i], 0);
            }
        }
        pairs.sort(Comparator.comparingInt(a -> a[0]));
        int num = 0, res = 0;
        for(int i = n-1; i >= 0; i--){
            if(num >= numWanted){
                break;
            }
            int val = pairs.get(i)[0], label = pairs.get(i)[1];
            if(limit.get(label) >= useLimit){
                continue;
            }
            res += val; num++; limit.put(label, limit.get(label)+1);
        }
        return res;
    }

    public static String decodeString(String s) {
        StringBuilder res = new StringBuilder();
        Deque<Character> queue = new ArrayDeque<>();
        for(char c: s.toCharArray()){
            if(c == ']'){
                StringBuilder str = new StringBuilder();
                StringBuilder num = new StringBuilder();
                while(!queue.isEmpty() && queue.peek() != '[')
                    str.append(queue.pop());
                if(!queue.isEmpty())
                    queue.pop();
                while(!queue.isEmpty() && Character.isDigit(queue.peek()))
                    num.append(queue.pop());
                str.reverse(); num.reverse();
                for(int i = 0; i < Integer.parseInt(num.toString()); i++){
                    // 重复 num 次，依次压入 queue
                    for(int j = 0; j < str.length(); j++){
                        queue.push(str.charAt(j));
                    }
                }
                continue;
            }
            queue.push(c);
        }
        while(!queue.isEmpty()){
            res.append(queue.pop());
        }
        return res.reverse().toString();
    }

    public static class LRUCache{

        private final int capacity;
        private static Map<Integer, Integer> map;
        private static Deque<Integer> deque;

        private static void update(int key){
            deque.remove(key);
            deque.offerFirst(key);
        }

        private static void delete(){
            if(deque.isEmpty()){
                return;
            }
            map.remove(deque.pollLast());
        }

        public LRUCache(int capacity){
            this.capacity = capacity;
            map = new HashMap<>();
            deque = new ArrayDeque<>(capacity);
        }

        public int get(int key){
            if(map.containsKey(key)){
                update(key);
                return map.get(key);
            }
            return -1;
        }


        public void put(int key, int value){
            if(map.containsKey(key)){
                update(key);
                map.put(key, value);
                return;
            }
            if(deque.size() >= capacity){
                Integer oldKey = deque.pollLast();
                map.remove(oldKey);
            }
            deque.offerFirst(key);
            map.put(key, value);
        }
    }


    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;
        for(int i = 0; i < n-2; i++){
            if(i > 0 && nums[i-1] == nums[i]){
                continue;
            }
            int left = i+1, right = n-1;
            while(left < right){
                int a = nums[i], b = nums[left], c = nums[right];
                if(a + b + c == 0){
                    res.add(Arrays.asList(a, b, c));
                    left++; right--;
                    while(right > left && nums[right] == nums[right+1]) right--;
                    while(left < right && nums[left] == nums[left-1])   left++;
                } else if (a + b + c > 0){
                    right--;
                    while(right > left && nums[right] == nums[right+1]) right--;
                } else {
                    left++;
                    while(left < right && nums[left] == nums[left-1])   left++;
                }
            }
        }
        return res;
    }


    public ListNode reverseList(ListNode head) {
        if(head == null){
            return head;
        }
        dfs(head);
        return Test.head;
    }

    private static ListNode head;
    public void dfs(ListNode p){
        if(p.next == null){
            head = p;
            return;
        }
        dfs(p.next);
        p.next.next = p;
        p.next = null;
    }


    public int longestPalindrome(String s) {
        Map<Character, Integer> map = new HashMap<>();
        for(Character c: s.toCharArray()){
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        int res = 0; boolean flag = false;
        for(Character c: map.keySet()){
            if(map.get(c) % 2 == 0){
                res += map.get(c);
            } else { // 奇数次也可以使用 n-1 次（偶数）去构造回文串
                flag = true;
                res += map.get(c)-1;
            }
        }
        return flag ? res + 1 : res;
    }



    public static int minPathSum(int[][] grid){
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        int sum = 0;
        for(int i = 0; i < m; i++){
            sum += grid[i][0];
            dp[i][0] = sum;
        }
        sum = 0;
        for(int i = 0; i < n; i++){
            sum += grid[0][i];
            dp[0][i] = sum;
        }
        for(int i = 1; i < m; i++){
            for(int j = 1; j < n; j++){
                int cur = grid[i][j];
                dp[i][j] = Math.min(dp[i-1][j], dp[i][j-1]) + cur;
            }
        }
        return dp[m-1][n-1];
    }

    public static void main(String[] args) {

    }
}
