package BayesC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jeasy.analysis.MMAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

public class BayesClassify{
	private String labelofnews=null;
	private long trainingTime=0;
	private long testingTime=0;
	public String[] labelsName=null;
	public Vector<Label> labelsList=null;
	public Set<String> vocabulary=new HashSet<String>();
	public String trainingFilePath=null;
	public String testingFilePath=null;
	
	public int findMax(double[] name){
		double max=name[0];
		int judge=0;
		for(int i=0;i<name.length;i++){
			if(name[i]>max){
				max=name[i];
				judge=i;
			}
		}
		return judge;
	}

	 Vector<String> readFile(String fileName) throws IOException, FileNotFoundException{
		File f=new File(fileName);
		InputStreamReader isr=new InputStreamReader(new FileInputStream(f),"GBK");
		char[] cbuf=new char[(int) f.length()];
		isr.read(cbuf);
		Analyzer analyzer=new MMAnalyzer();
		TokenStream tokens=analyzer.tokenStream("Contents", new StringReader(new String(cbuf)));
		Token token=null;
		Vector<String> textList=new Vector<String>();
		while((token=tokens.next(new Token()))!=null){
			textList.add(token.term());
		}
		return textList;
	}

	
	public void setTrainPath(String TrainPath){
		trainingFilePath=TrainPath;
		//train();
	}
	
	public String[] sort(String[] data, int left, int right){
		String middle,strTemp;
		int i = left;
		int j = right;
		middle = data[(left+right)/2];
		do{
			while((data[i].compareTo(middle)<0) && (i<right))
				i++;
			while((data[j].compareTo(middle)>0) && (j>left))
				j--;
			if(i<=j){
				strTemp = data[i];
				data[i] = data[j];
				data[j] = strTemp;
				i++;
				j--;
			}
		}while(i<j);
		if(left<j)
			sort(data,left,j);
		if(right>i)
			sort(data,i,right); 
		return data;
	}

	public void train() {
		long startTrainTime=System.currentTimeMillis();
		File folder=new File(trainingFilePath);
		labelsName=folder.list();
		labelsList=new Vector<Label>();
		for(int i=0;i<labelsName.length;i++){
			labelsList.add(new Label());
			File subFolderList=new File(trainingFilePath+"\\"+labelsName[i]);
			String[] filesList=subFolderList.list();
			System.out.println("Now processing:"+labelsName[i]);
			GUI.setTextArea("Now processing:"+labelsName[i]);
			Vector<String> text=new Vector<String>();
			for(int j=0;j<filesList.length;j++){
				//System.out.print(files[j]+" ");
				try {
					text.addAll(readFile(trainingFilePath+"\\"+labelsName[i]+"\\"+filesList[j]));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//put all word from current label to the set
			//get the size of vocabulary
			//sort for all words，
			//in order to calculate the word information 
			String[] allWords=new String[text.size()];
			for(int j=0;j<text.size();j++)
				allWords[j]=text.elementAt(j);
			sort(allWords,0,text.size()-1);
		
			String previous=allWords[0];
			double count=1;
			Map<String,WordInfor> trainMap=new HashMap<String, WordInfor>();
			for(int j=1;j<allWords.length;j++){
				if(allWords[j].equals(previous))
					count++;
				else{
					vocabulary.add(previous);
					trainMap.put(previous, new WordInfor(count));
					previous=allWords[j];
					count=1;
				    }
			}
			labelsList.elementAt(i).set(trainMap, text.size(),filesList.length);
			long endTrainTime=System.currentTimeMillis();
			trainingTime=endTrainTime-startTrainTime;
		}
		//Got the size of vocabulary, then calculate the frequency
		for(int i=0;i<labelsList.size();i++){
			Iterator iter=labelsList.elementAt(i).map.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, WordInfor> entry=(Map.Entry<String, WordInfor>)iter.next();
				WordInfor item=entry.getValue();
				item.setFrequency(Math.log10((item.count+1)/(labelsList.elementAt(i).wordCount+vocabulary.size())));//using m-estimate here
			}
		}
	}

	
	public void setTestPath(String testPath){
		testingFilePath=testPath;
		//test();
	}
		public void test(){
			long startTestTime=System.currentTimeMillis();
			File folder=new File(testingFilePath);
			String []ln;
			ln=folder.list();
			for(int x=0;x<ln.length;x++){
			Vector<String> v=null;
			try {
				v = readFile(testingFilePath+"\\"+ln[x]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double values[]=new double[labelsName.length];
			for(int i=0;i<labelsList.size();i++){
				double tempValue=labelsList.elementAt(i).textCount;
				for(int j=0;j<v.size();j++){
					if(labelsList.elementAt(i).map.containsKey(v.elementAt(j))){
						tempValue+=labelsList.elementAt(i).map.get(v.elementAt(j)).fre;
					}else{
						tempValue+=Math.log10(1/(double)(labelsList.elementAt(i).wordCount+vocabulary.size()));//log here make * to +
					}
				}
				values[i]=tempValue;
			}
			//for(int i=0;i<values.length;i++)
				//System.out.println(labelsName[i]+"'s probability is"+values[i]);
			int maxIndex=findMax(values);
			System.out.println(testingFilePath+" belongs to "+labelsName[maxIndex]);
			GUI.setTextArea(testingFilePath+" belongs to "+labelsName[maxIndex]);
			labelofnews=labelsName[maxIndex];
		}
			long endTestTime=System.currentTimeMillis();
			testingTime=endTestTime-startTestTime;
		}
		
		public String getLabelName(){
			return labelofnews;
		}
		public long getTrainingTime(){
			return trainingTime;
		}
		public long getTestingTime(){
			return testingTime;
		}
}
class Label{//For Text's label: computer, social
	//map for storing every single word and its calculation 
	Map<String,WordInfor> map=new HashMap<String,WordInfor>();
	double wordCount;//The sum of words of one specific label
	double textCount;//The sum of texts of one label
	public Label() {
		this.map=null;
		this.wordCount=-1;
		this.textCount=-1;
	}
	public void set(Map<String,WordInfor> m,double wordCount,double documentCount) {
		this.map=m;
		this.wordCount=wordCount;
		this.textCount=documentCount;
	}
}
class WordInfor{//This class for word information, including frequency and number
	double fre;//words frequency which should be calculated after the vocabulary has been set
	double count;//number of words
	public WordInfor(double count) {
		this.fre=-1;
		this.count=count;
	}
	public void setFrequency(double frequency){
		this.fre=frequency;
	}
}
