package launchers;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NodeLauncher implements Runnable{
    private String[] pair;


    public NodeLauncher(String[] pair){
        this.pair = pair;
    }

    public static void main(String[] args) {

        List<String[]> pairs = new ArrayList<>();

        while(true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Insert id and port of nodes separated by a space (e.g. 10 8999)\nInsert start to initialize nodes\nInsert del to delete the last node added");
            String input = scanner.nextLine();

            if (input.toLowerCase().equals("start")) {
                System.out.println("Initializing nodes ...");
                break;
            }else if(input.toLowerCase().equals("del")){
                if(pairs.size() > 0) {
                    pairs.remove(pairs.size() - 1);
                }
            }else{
                String[] pair = input.split(" ");
                try {
                    if (Integer.parseInt(pair[0]) > 0 && 1024 < Integer.parseInt(pair[1]) && Integer.parseInt(pair[1]) < 65537) {
                        pairs.add(pair);
                    }
                }catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
                    System.out.println("Input is not valid...");
                }
            }
        }

        Thread[] threads = new Thread[pairs.size()];
        int i = 0;
        for(String[] pair : pairs){
            threads[i] = new Thread(new NodeLauncher(pair));
            i++;
        }

        for(Thread t : threads){
            t.start();
        }
    }

    @Override
    public void run() {
        node.Node.main(new String[]{pair[0], pair[1]});

    }
}
