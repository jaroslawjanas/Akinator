import javax.swing.*;
import java.io.*;

public class Game implements java.io.Serializable {

//    options array for the menu
    String[] options= {"Play", "Save", "Load", "Quit"};

    Game() {
//        create a variable to store a BinaryTree in
        BinaryTree tree;

//        load a tree.ser file from
        tree=(BinaryTree) load("trees\\tree.ser");
//        if returns null (file doesn't exist)
        if(tree==null) {
//            create a new tree
            tree = createTree();
//            save tree to "trees\\tree.ser"
            save("trees\\tree.ser", tree);
        }

//        uselessPrintMethodWeWereAskedToWriteForTesting(tree);

//         variable for storing user's choise of action
        int action;
        while(true) {
//            menu, presents user with choice of action: PLAY , SAVE , LOAD , QUIT
            action = JOptionPane.showOptionDialog(null, "What would you like to do?", "Guessing Game", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options);

//            if PLAY call the play method
            if(action == 0) {
                play(tree);
//                uselessPrintMethodWeWereAskedToWriteForTesting(tree);
            }

//            if SAVE, save the CURRENT tree to the "trees" folder
            if (action == 1)
                save("trees\\tree.ser", tree);

//            if LOAD, load the currently saved tree from the "trees" folder
            if (action == 2)
                tree = (BinaryTree) load("trees\\tree.ser");

//            if QUIT, quit the program
            if (action == 3 || action==JOptionPane.CLOSED_OPTION)
                System.exit(-1);
        }
    }

//    play method for playing the game
    public void play(BinaryTree tree){
//        variable for storing the previous node
        BinaryNodeInterface previous=null;
//        variable for storing the current node
//        gets the root node at the start of the game
        BinaryNodeInterface current=tree.getRootNode();
//        store the previous answer to the question
//        to know where to insert new nodes, when the final
//        answer is wrong
        Boolean previousAnswer=null;

//        just a loop, no need for a condition as break is used for efficiency
        while(true){
//            calls the question() method with the current node as an argument
            Boolean answer=question(current, null);
            Boolean isALeaf= current.isLeaf();

//            YES answer to the question but not a leaf
            if(answer && !isALeaf){
//                assigns the current node as previous
                previous=current;
//                the answer to the question was YES
//                hence assign current's left child as current
                current=current.getLeftChild();
                previousAnswer=true;
            }

//            YES answer to the question and is a leaf (final answer)
            else if(answer && isALeaf){
//                correct guess
                break;
            }

//            NO answer to the question but not a leaf
            else if(!answer && !isALeaf){
                previous=current;
//                the answer to the question was NO
//                hence assign current's right child as current
                current=current.getRightChild();
                previousAnswer=false;
            }

//            NO answer to the question and is a leaf
            else if(!answer && isALeaf){
//                ask for the correct answer
                String correctAnswer=askForAnwser();
//                ask for the correct question to the answer
                String correctQuestion=askForQuestion(previous, correctAnswer);

//                create a new binary tree with the correct question to the answer
                BinaryTree temp = new BinaryTree (correctQuestion);
//                ask what is the answer (YES/NO) to the new question and new answer
//                to know if the new answer should be a righ/left child of the new question
                if(question(temp.getRootNode(), correctAnswer)){
                    temp.getRootNode().setLeftChild(new BinaryTree("Is it "+correctAnswer+"?").getRootNode());
                    temp.getRootNode().setRightChild(current);
                }
                else{
                    temp.getRootNode().setLeftChild(current);
                    temp.getRootNode().setRightChild(new BinaryTree("Is it "+correctAnswer+"?").getRootNode());
                }

                if(previousAnswer==true)
                    previous.setLeftChild(temp.getRootNode());

//                if the answer to the previous question was NO
//                link the new node/tree as a right child
                else if(previousAnswer==false)
                    previous.setRightChild(temp.getRootNode());

                break;
            }

//            missing answer, next node == null
            if(current==null){
                JOptionPane.showMessageDialog(null, "Sorry, I do not have an answer for that!  ");
//                ask for the correct answer
                String correctAnswer=askForAnwser();
//                ask for the correct question to the answer
                String correctQuestion=askForQuestion(previous, correctAnswer);

//                create a new binary tree with the correct question to the answer
                BinaryTree temp = new BinaryTree (correctQuestion);
//                ask what is the answer (YES/NO) to the new question and new answer
//                to know if the new answer should be a righ/left child of the new question
                if(question(temp.getRootNode(), correctAnswer)){
                    temp.getRootNode().setLeftChild(new BinaryTree("Is it "+correctAnswer+"?").getRootNode());
                    temp.getRootNode().setRightChild(current);
                }
                else{
                    temp.getRootNode().setLeftChild(current);
                    temp.getRootNode().setRightChild(new BinaryTree("Is it "+correctAnswer+"?").getRootNode());
                }

                if(previous.getLeftChild()==null) {
                    previous.setLeftChild(temp.getRootNode());
                }
//                if the answer to the previous question was NO
//                link the new node/tree as a right child
                else if(previous.getRightChild()==null){
                    previous.setRightChild(temp.getRootNode());
                }
                break;
            }

        }
    }

//    this method creates the initial tree for the game
    public BinaryTree createTree(){
        BinaryTree<String> penguin = new BinaryTree<String>("Is it a Penguin?");
        BinaryTree<String> canFly = new BinaryTree<String>("Can it fly?", null, penguin);
        BinaryTree<String> isBird = new BinaryTree<String>("Is it a bird?", canFly, null);
        BinaryTree<String> isMammal = new BinaryTree<String>("Is it a mammal?", null, isBird);
        BinaryTree<String> tree = new BinaryTree<String>("Is it an animal?", isMammal, null);
        return tree;
    }

//    this method save an object
//    in this case the tree object that is being passed in
//    as an argument, together with the destination

//    This post was created by me
//    https://stackoverflow.com/questions/55345284/how-do-i-write-a-file-to-the-correct-directory-whether-the-program-is-run-from-a/55345575#55345575
//    It's NOT COPIED from someone !!!
    public void save(String filepath, Object obj) {
//        get the folder
//        if no folder create a new one
        File folder = new File("trees");
        if (!folder.exists()){
            folder.mkdirs();
        }

        try {
            FileOutputStream file = new FileOutputStream(filepath);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(obj);
            out.close();
            file.close();
        } catch (IOException ex) {
            System.out.println("IOException is caught");
        }
    }

//    this method loades the saved object
//    from a location passed in as an argument
    public Object load(String filepath) {
        Object obj = null;
        try {
            // reading from the file
            FileInputStream file = new FileInputStream(filepath);
            ObjectInputStream in = new ObjectInputStream(file);

            // deserialization
            obj = in.readObject();

            in.close();
            file.close();

        } catch (IOException ex) {
            System.out.println("IOException");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException");
        }
        return obj;
    }

//    this method asks the user question stored in a node
//    that is being passed in as an argument
//    this method also allows for asking the correct answer
//    to the new question and answer
    public boolean question(BinaryNodeInterface node, String answer){
//        gets the question from the node in form of a string
        String q = node.getData().toString();
        int reply;

//        this is for asking the answer
//        to the new question and new answer
        if(answer!=null)
            reply = JOptionPane.showConfirmDialog(null, "Answer this question about "+answer+".\n"+q, "Guessing Game", JOptionPane.YES_NO_OPTION);

//        shows the question with two reply options, YES and NO
        else
            reply = JOptionPane.showConfirmDialog(null, q, "Guessing Game", JOptionPane.YES_NO_OPTION);

//        checks if the window with the question was closed
//        if yes, exists the game
        if(reply==JOptionPane.CLOSED_OPTION)
            System.exit(-1);

//        checks if reply to the question was YES, if so returns true
//        if reply to the question was NO returns false
        if(reply==JOptionPane.YES_OPTION)
            return true;
        else
            return false;

    }

//    this method asks for the correct answer
    public String askForAnwser(){
        String reply = JOptionPane.showInputDialog(null, "What were you thinking of? In your answer use \"a\" and \"an\" if appropriate!");
//        if window is closed, or the reply is empty
//        the game will exit
        if(reply==null)
            System.exit(-1);
        return reply;
    }

//    this method asks for the correct question
    public String askForQuestion(BinaryNodeInterface previous, String correctAnswer){
//        get the previous question
        String previousQuestion = previous.getData().toString();
//        display the correct answer and question so that the new question can be chosen by the user
        String reply = JOptionPane.showInputDialog(null, "What question differentiates "+correctAnswer+" ?\nPrevious question: "+previousQuestion);

//        if window is closed, or the reply is empty
//        the game will exit
        if(reply==null)
            System.exit(-1);

        return reply;
    }

    public void uselessPrintMethodWeWereAskedToWriteForTesting(BinaryTree tree){
        tree.inorderTraverse();
    }

}
