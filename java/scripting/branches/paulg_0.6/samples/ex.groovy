import javax.swing.JFrame
import javax.swing.JTree
import javax.swing.WindowConstants

class SimplestGUI {
   void buildIt() {
      frame = new JFrame("Simplest GUI");
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      tree = new JTree();
      frame.getContentPane().add(tree);
      frame.pack();
      frame.show();
   }
  
   static void main(args) {
      b = new SimplestGUI()
      b.buildIt()
   }
}