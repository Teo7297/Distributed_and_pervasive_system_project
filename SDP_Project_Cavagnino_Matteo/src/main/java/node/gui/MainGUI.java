package node.gui;

import node.Node;

import javax.swing.*;


public class MainGUI {
    public static void spawnGUI(String id, Node node){
        JFrame frame = new JFrame("NODE [ " + id + "] GUI" );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        JButton button1 = new JButton("Press to kill the node [ " + id + " ]");
        button1.addActionListener(e -> {
            node.setExitFlag(true);
        });
        frame.getContentPane().add(button1);
        frame.setVisible(true);
    }

}