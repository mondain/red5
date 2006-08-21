require 'java'
module JavaSwing
  include_package "javax.swing"
  include_package "java.awt.event"
end

frame = JavaSwing::JFrame.new("Simple Ruby App")
tree = JavaSwing::JTree.new()
frame.getContentPane().add(tree)
frame.setDefaultCloseOperation(JFrame::EXIT_ON_CLOSE)
frame.pack()
frame.setVisible(true)
