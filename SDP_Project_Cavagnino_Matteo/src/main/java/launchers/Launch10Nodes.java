package launchers;


public class Launch10Nodes implements Runnable{
    private final int i;


    public Launch10Nodes(int i){
        this.i = i;
    }

    public static void main(String[] args) {

        int max = 10;

        Thread[] threads = new Thread[max];
        for(int i = 0; i < max; i++){
            threads[i] = new Thread(new Launch10Nodes(i));
        }

        for(Thread t : threads){
            t.start();
        }

    }

    @Override
    public void run() {
        node.Node.main(new String[]{""+(320+i), ""+(i+7240)});
    }

}
