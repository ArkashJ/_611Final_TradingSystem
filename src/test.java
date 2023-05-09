import main.PortfolioManager.BankManager;

import java.util.Map;

public class test {
    public static void main(String[] args) {
//        Map<String, Double> profits = BankManager.calculateProfits(11);
//        System.out.println(profits);
        int[] nums1={2};
        int[] nums2={1,3};
        double res=findMedianSortedArrays(nums1,nums2);
        System.out.println(res);
    }

    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int totalLength=nums1.length+nums2.length;
        if(nums1.length > nums2.length) {
            int[] tmp=nums1;
            nums1=nums2;
            nums2=tmp;
        }

        boolean isOdd=totalLength %2 != 0 ;
        int l=0;
        int r=nums1.length-1;
        while(true) {
            int i=(l+r)/2;
            int j=totalLength/2-(i+1)-1;
            int A_left= i<0 ? Integer.MIN_VALUE : nums1[i];
            int B_left=j<0 ? Integer.MIN_VALUE : nums2[j];
            int A_right=(i+1)<nums1.length ? nums1[i+1] : Integer.MAX_VALUE;
            int B_right= (j+1) < nums2.length ? nums2[j+1] : Integer.MAX_VALUE;

            // if petition is right
            if(A_left<=B_right && B_left <= A_right) {
                //isOdd
                if(isOdd) {
                    return Math.min(A_right,B_right);
                }
                else {
                    return (double)(Math.max(A_left,B_left) +Math.min(A_right,B_right))/2;
                }
            }
            else if(A_left > B_right) {
                r=i-1;
            }
            else {
                l=i+1;
            }
        }

    }
}
