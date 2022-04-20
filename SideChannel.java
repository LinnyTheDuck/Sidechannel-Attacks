// Stefenie Pickston 1506427

public class SideChannel {
    public static void main(String[] args) {
        try{
            if(args.length != 1){
                System.err.println("Usage: java SideChannel <option> > output.txt");
                System.err.println("Options are: \n 0 = Preliminary Test \n 1 = Measure Individual Cache Hits (Config for L3) \n 2 = Measure Cache Line Iteration");
            }
            else{
                if(args[0].equals("0"))
                    prelim();
                else if(args[0].equals("1"))
                    single();
                else if (args[0].equals("2"))
                    accumulative();
                else{
                    System.err.println("Usage: java SideChannel <option> > output.txt");
                    System.err.println("Options are: \n 0 = Preliminary Test \n 1 = Measure Individual Cache Hits (Config for L3) \n 2 = Measure Cache Line Iteration");
                }
            }
        } catch (Exception e) {
            System.err.println("Something Bad"); // some Ethan style maths has happened here
            e.printStackTrace();
        }
    }

    public static long total = 0; // calculate total time

    // runs different cache line sizes
    public static void prelim(){
        for(int K = 1; K < 513; K *= 2){ // interate through each cache line size
            int[] arr = new int[64 * 1024 * 1024];
            long start = System.nanoTime(); // start measuring
            for (int i = 0; i < arr.length; i += K) arr[i] *= 3;
            long end = System.nanoTime(); // stop measuring
            System.out.println((end - start) + "," + K);
        }
    }

    // measures the timing for each cache hit individually
    public static void single(){
        // 2 cores
        // 1024 * kb want to check (but go a little bigger incase overwrapping for L2 and 3)
        // 128 kib L1 => 64 kib in each l1 cache
        // 512 kib L2 => 640 kib total
        // 3 mib L3 => 3072 kib, 3712 kib total
        // 256, 1024 (L1), 4096, 8192 (L2), 32768 (L3) as i increment to agregate plot points        

        // this is for the L3 cache measurements individually

        for(int i = 8; i < 1024*4000; i += 32768){ // changing the size of the input per loop iteration
            System.gc(); // run the garbage collector before loop to cleanse data
            long total = test(i); // run test
            System.out.println(total + "," + Math.floorDiv(i, 1000));
        }
    }

    // measures the time for each cache line for each iteration
    public static void accumulative(){
        for(int i = 1; i < 17; i ++){ // changing the size of the input per loop iteration
            System.gc(); // run the garbage collector before loop to cleanse data
            long total = test((int)Math.pow(2, i) * 1024); // run test
            System.out.println(total + "," + (int)Math.pow(2, i));
        }
    }

    // & vs %
    // change the value of K
    // using int arrays vs byte arrays
    // array size is 4x ints inside
    // 16 -> 64 makes less noise
    public static long test(int size){
        byte[] arr = new byte[size];
        int steps = 64 * 1024 * 1024; // Arbitrary number of steps
        int lengthMod = arr.length - 1;
        
        arr[0] = 1; // microwaving and reheating last nights cache but the microwave is broken
        
        //for(int i = 0; i < size; i += 64) // I think I fixed the microwave
        //    arr[i] = 1;

        long start = System.nanoTime(); // start measuring
        for (int i = 0; i < steps; i += 64) // k = 4096, change to line size
        {
            arr[(i * 64) & lengthMod]++; // (x & lengthMod) is equal to (x % arr.Length)
        }
        long end = System.nanoTime(); // stop measuring
        return end - start; // calculate total and output it for single measurement

        //total += (end - start); // calculate accumulative total NOT NEEDED
        //return total;
    } 
}
