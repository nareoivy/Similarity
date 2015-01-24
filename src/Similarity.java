/*
Author:Na
Data :2014/11/29
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Similarity{
	String sentence="";
	String []part=new String[2];
	String input,output,standardAnalysis;
	List <String> comDataList=new ArrayList<String>();
	List <String> userDataList=new ArrayList<String>();
	static float[] comData=new float[1000];
	static float[] userData=new float[1000];
	public Similarity(String input,String output,String standardAnalysis){
		this.input=input;
		this.output=output;
		this.standardAnalysis=standardAnalysis;
	}
	public static void main(String args[]) throws IOException{
		Similarity s=new Similarity("E://input.txt","E://output.txt","E://standardAnalysis.txt");
		s.fileOperation(s.input,s.output);
		System.out.println("������������ƶȷ������"+"\n"+"��鿴�ļ���"+s.output);
		System.out.println("......"+"\n"+"Pearson correlation��������");
		System.out.println("������������˹���ֽ��֮�����ض��ǣ�");
		s.PearsonFileOperation(s.output,s.standardAnalysis);
		float pearson=s.Pearson(comData,userData);
		System.out.println(pearson);
	
		//System.out.println(s.Sum(comData));
		//System.out.println(s.Sum(userData));
		
	}
	
	//Chapter1:����������
	public void fileOperation(String inputPath,String outputPath) throws IOException{
		//���ļ�
		File inputFile=new File(inputPath);
		BufferedReader reader=null;
        //if(!inputFile.exists()||inputFile.isDirectory())
		//throw new FileNotFoundException();
        try{
        	reader=new BufferedReader(new FileReader(inputFile));
        }catch(Exception e){
        	System.out.println("�����ļ�����"+e.getMessage());
        }
        
        //д�ļ�
        File outputFile=new File(outputPath);
        FileWriter writer=null;
        if(!outputFile.exists())
        	if(!outputFile.createNewFile())
        		System.out.println("����ļ�����ʧ��");
        writer=new FileWriter(outputFile);
        
        //���еõ����ӶԶ�
        int line=1;
        float result=(float) 0.0;
        String tmpToWrite="";
        while((sentence=reader.readLine())!=null){
            part=sentence.split("\t");//��"tab"��ÿ�Զ����ӷֳ�������part[0],part[1]
            line++; 
            result=cosVector(part[0],part[1]); //�����������������ƶ�   
            
            // ����       ���ƶ�d+"\tab"+part[0]+"\tab"+part[1]+"\n"  
            tmpToWrite=result+"\t"+part[0]+"\t"+part[1]+"\r\n";
            writer.write(tmpToWrite);
            writer.flush();
        }
        if(reader!=null){
        	try{
        		reader.close();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        if(writer!=null){
        	try{
        		writer.close();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
	}
	
	//�ж�ָ���ַ���str�Ƿ���Map������������
	public boolean isIn(Map<String,int[]> wordWeight,String str){
		for (String key : wordWeight.keySet()) {//����map������key
			if(key.equals(str))
				return true;
		}
		return false;
	}
	
	//������������
	public float cosVector(String sentence1,String sentence2){
		String []wordsOfSen1=new String[64];//��һ��ĵ��ʼ�
		String []wordsOfSen2=new String[64];//�ڶ���ĵ��ʼ�
		wordsOfSen1=sentence1.split(" ");
	    wordsOfSen2=sentence2.split(" ");
	    //���ʵĳ���Ƶ��������wordWeight[word][0]����"word"�ڵ�һ���г��ֵ�Ƶ��
		Map <String,int[]> wordWeight=new HashMap<String ,int[]>();
		
		//���仰�ĵ���Ƶ��ͳ��
	    for(int i=0;i<wordsOfSen1.length;i++){
	    	if(!isIn(wordWeight,wordsOfSen1[i]))
	    		wordWeight.put(wordsOfSen1[i], new int[]{1,0});
	    	else
	    		wordWeight.get(wordsOfSen1[i])[0]+=1;
	    }
	    for(int i=0;i<wordsOfSen2.length;i++){
	    	if(!isIn(wordWeight,wordsOfSen2[i]))
	    		wordWeight.put(wordsOfSen2[i], new int[]{0,1});
	    	else
	    		wordWeight.get(wordsOfSen2[i])[1]+=1;
	    }
	    //�����Ѿ����������ʵ�Ƶ����������(����������)����ʽ��ʾ������
	    //wordWeight.size����������ά��
	    //wordWeight[word][0]���ǵ���"word"�ڵ�һ���г��ֵ�Ƶ��
	    //�������ø�������������
	    float neiji=(float) 0.0;//���������������ڻ�
	    float modeOfSen1=(float)0.0;//����1������ģdeƽ��
	    float modeOfSen2=(float)0.0;//����2������ģdeƽ��
	    for(String key:wordWeight.keySet()){
	    	neiji+=wordWeight.get(key)[0]*wordWeight.get(key)[1];
	    	modeOfSen1+=Math.pow(wordWeight.get(key)[0], 2);
	    	modeOfSen2+=Math.pow(wordWeight.get(key)[1], 2);
	    	
	    }
		return (float) (neiji/(Math.sqrt(modeOfSen1)*Math.sqrt(modeOfSen2)));
	}
	
	
	
	//Chapter2:Pearson�ع����
	//Pearson��ʽ
	public float Pearson(float[] x,float[] y){
		int lenx=x.length;
		int leny=y.length;
		int len=lenx;//С�ݴ�
		if(lenx<leny) len=lenx;
		else len=leny;
		
		float sumX=Sum(x);
		float sumY=Sum(y);
		float sumXX=Mutipl(x,x,len);
		float sumYY=Mutipl(y,y,len);
		float sumXY=Mutipl(x,y,len);
		float upside=sumXY-sumX*sumY/len;
		float downside=(float) Math.sqrt((sumXX-(Math.pow(sumX, 2))/len)*(sumYY-(Math.pow(sumY, 2))/len));
		System.out.println(len+" "+sumX+" "+sumY+" "+sumXX+" "+sumYY+" "+sumXY);
		return upside/downside;
	}
	public float Sum(float[] arr){
		float total=(float)0.0;
		for(float ele:arr)
			total+=ele;
		return total;
	}
	public float Mutipl(float[] arr1,float[] arr2,int len){
		float total=(float)0.0;
		for(int i=0;i<len;i++)
			total+=arr1[i]*arr2[i];
		return total;
	}
	//String����תΪfloat����
	public float[] strToFloat(List<String> str){
		int len=str.size();
		float[] floatArr=new float[len];
		for(int i=0;i<len;i++){
			floatArr[i]=Float.parseFloat(str.get(i));
		}
		
		return floatArr;
	}
	public float PearsonFileOperation(String outputPath,String standardAnalysisPath) throws FileNotFoundException  {
		//���ļ�
		File outputFile=new File(outputPath);
		BufferedReader reader1=null;
		if(!outputFile.exists()||outputFile.isDirectory())
				throw new FileNotFoundException();
		reader1=new BufferedReader(new FileReader(outputFile));
		
		File standardAnalysisFile=new File(standardAnalysisPath);
		BufferedReader reader2=null;
		if(!standardAnalysisFile.exists()||standardAnalysisFile.isDirectory())
				throw new FileNotFoundException();
		reader2=new BufferedReader(new FileReader(standardAnalysisFile));
		
		//�ֶ�
		String tmpSen="";
		String []tmpPart=new String[6];
		try {
			while((tmpSen=reader1.readLine())!=null){
				tmpPart=tmpSen.split("\t");
				comDataList.add(tmpPart[0]);
			}
			while((tmpSen=reader2.readLine())!=null){
				tmpPart=tmpSen.split("\n");
				userDataList.add(tmpPart[0]);
			}
			//��listת��Ϊfloat����
			comData= strToFloat(comDataList);
			userData= strToFloat(userDataList);
			
		} catch (IOException e) {
			System.out.println("����");
			e.printStackTrace();
		}
		return 0;
	}
	
}