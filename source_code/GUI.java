package BayesC;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import java.awt.*;




public class GUI extends JFrame implements Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static BayesClassify bayes=new BayesClassify();
	private static JTextArea messageArea;
	private static final int DEFAULT_WIDTH=564;
	private static final int DEFAULT_HEIGHT=471;
	private  boolean flagT=false;
	private boolean flagTe=false;
	private static JPanel Inforpanel =null;
	 
	public GUI() {
		super();
		
		
			((JPanel)this.getContentPane()).setOpaque(false);
			ImageIcon img = new ImageIcon
					("C:\\Users\\13269\\Desktop\\Java\\Bayes\\Bayes.jpg");
			JLabel background = new JLabel(img);
			this.getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
			background.setBounds(0,0,img.getIconWidth(),img.getIconHeight());
		
		getContentPane().setLayout(null);
		setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		
		
		
		final JButton button0 = new JButton();
		button0.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
			    JFileChooser chooser = new JFileChooser();
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    int   n = chooser.showOpenDialog(getContentPane());  
	            if(n == JFileChooser.APPROVE_OPTION){
					bayes.setTrainPath(chooser.getSelectedFile().getPath());
					flagT=true;
					run();
	            }
			}
		});
		
		button0.setText("Train");
		button0.setBounds(93, 64, 106, 28);
		getContentPane().add(button0);
		this.getContentPane().setBackground(Color.yellow);
           
		JTextField text = new JTextField();
		text.setText("Bayes Classify(╯‵□′)╯︵┻━┻");
		text.setBounds(180,30,190,30);
		getContentPane().add(text);
		
		final JButton button1 = new JButton();
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				 String dic;
				 JFileChooser chooser = new JFileChooser();
				  chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				  int   n = chooser.showOpenDialog(getContentPane());  
		            if(n == JFileChooser.APPROVE_OPTION){
				  dic=chooser.getSelectedFile().getPath();
				bayes.setTestPath(dic);
				flagTe=true;
				run();
				}
		            
				
			}
		});
		button1.setText("Test");
		button1.setBounds(325, 64, 106, 28);
		getContentPane().add(button1);

		messageArea = new JTextArea();	
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true); 
		//getContentPane().add(textArea);
		JScrollPane scroll=new JScrollPane(messageArea);
		scroll.setBounds(68, 116, 412, 246);	
		scroll.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scroll);
		
		setVisible(true);
		scroll.setVisible(true);
		Toolkit toolkit =this.getToolkit();
		Dimension dim = toolkit.getScreenSize();
		this.setLocation((int)(dim.getWidth()-DEFAULT_WIDTH)/2,(int)(dim.getHeight()-DEFAULT_HEIGHT)/2);
	}
	private void setBg() {
		// TODO Auto-generated method stub
		
	}
	public static void setTextArea(String s){
		//textArea.append(s+"\n");
		messageArea.insert(s+"\n", 0);
	}
	public static void main(String[] args){
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
				}
				GUI gui=new GUI();
				gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				gui.setVisible(true);
				Thread t=new Thread(gui);
				t.start();
//			}
//		});
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if(flagT){
				bayes.train();
				flagT=false;
				JOptionPane.showMessageDialog(Inforpanel,"Training is end!\nTotally cost"+bayes.getTrainingTime()+" ms");
			    return;
			}
			if(flagTe){
				bayes.test();
				flagTe=false;
				JOptionPane.showMessageDialog(Inforpanel,"Testing is end!\nTotally cost"+bayes.getTestingTime()+" ms");
				return;
			}
		}
	}
}
