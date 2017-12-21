import java.util.ArrayList;
import java.io.*;


class Predicate {
	String predicateName;
	ArrayList<String> arguments = new ArrayList<>();
	boolean isNegation;

	Predicate(){}

	Predicate(String predicateName, ArrayList<String> argumentList, boolean isNegated)
	{
		this.predicateName = predicateName;
		this.isNegation = isNegated;
		this.arguments = argumentList;
	}

	Predicate(Predicate p)
	{
		this.predicateName = p.predicateName;
		this.isNegation = p.isNegation;
		for(String v : p.arguments)
		{
			this.arguments.add(new String(v));
		}
	}
}

class Sentence
{
	ArrayList<Predicate> predicateList = new ArrayList<>();

	Sentence(){}

	Sentence(ArrayList<Predicate> predicates)
	{
		this.predicateList = predicates;
	}

	Sentence(Sentence s){
		for(Predicate p:s.predicateList){
			Predicate p2 = new Predicate(p);
			this.predicateList.add(p2);
		}
	}

	void addPredicate( Predicate p)
	{
		predicateList.add(p);
	}
}

class KnowledgeBase
{
	ArrayList<Sentence> sentenceList;
	public KnowledgeBase(ArrayList<Sentence> sentences)
	{
		this.sentenceList = sentences;

	}

	void addSentence(Sentence s)
	{
		sentenceList.add(s);
	}
}

class StandardizationOfVars{
	ArrayList<String> variableList = new ArrayList<>();
	int count = 1;

	StandardizationOfVars(ArrayList<Sentence> sentenceList){
		for(Sentence s: sentenceList){
			for(Predicate predicate: s.predicateList){
				for(int i=0; i< predicate.arguments.size();i++){
					String varOfPredicate = predicate.arguments.get(i);
					if(Character.isLowerCase(varOfPredicate.charAt(0))){
						//argument = argument + Integer.toString(sentenceCounter);
						String newV = varOfPredicate.charAt(0) + Integer.toString(count);
						predicate.arguments.set(i,newV);
						variableList.add(newV);
					}
				}
			}
			++count;
		}
	}
}

public class homework {
	long maxTime = 200;
	long start;

	boolean PredicateEqualityCheck(Predicate p1, Predicate p2)
	{
		if(p1.predicateName!= p2.predicateName) return false;
		if(p1.arguments.size()!= p2.arguments.size()) return false;
		if(p1.isNegation!= p2.isNegation) return false;
		int size =  p1.arguments.size();
		for(int i=0;i<size;i++)
		{

			if(!(p1.arguments.get(i).equals(p2.arguments.get(i))))
				return false;


		}
		return true;
	}

	boolean sentenceEqualityCheck(Sentence s1, Sentence s2){
		if(s1.predicateList.size() != s2.predicateList.size()) return false;
		int size = s1.predicateList.size();
		int hit = 0;
		for(Predicate p1:s1.predicateList){
			for(Predicate p2:s2.predicateList){
				if(PredicateEqualityCheck(p1,p2)){
					hit++;
					break;
				}
			}
		}
		if(hit == size)
			return true;
		else return false;

	}
	
	boolean checkRepeatedPredicates(Predicate p1, Predicate p2) {
		if (p1.predicateName.equals(p2.predicateName) && p1.arguments.size() == p2.arguments.size() && p1.isNegation == p2.isNegation) {
			int size = p1.arguments.size();
			for (int i = 0; i < size; i++) {
				if (!(p1.arguments.get(i).equals(p2.arguments.get(i)))) {
					return false;
				}
			}
			return true;
		} else
			return false;
	}
	
	void removeRepeatingPredicates(Sentence s) {
		int position = -1;
		for(int i = 0; i < s.predicateList.size() - 1; i++) {
			for (int j = i + 1; j < s.predicateList.size(); j++) {
				if (checkRepeatedPredicates(s.predicateList.get(i), s.predicateList.get(j))) {
					position = j;
				}
			}
		}
		while(position != -1){
			s.predicateList.remove(position);
			position = -1;
			for(int i = 0; i < s.predicateList.size() - 1; i++) {
				for (int j = i + 1; j < s.predicateList.size(); j++) {
					if (checkRepeatedPredicates(s.predicateList.get(i), s.predicateList.get(j))) {
						position = j;
					}
				}
			}
		}
	}
	
	static Sentence ConvertStringToSentence(String str) {

		Predicate p = new Predicate();
		Sentence sentence;
		ArrayList<Predicate> predicateList = new ArrayList<Predicate>();

		char arr[] = str.toCharArray(); int n= arr.length;

		for(int i=0;i<n;i++)
		{
			ArrayList<String> argList = new ArrayList<>();

			if(arr[i]=='~' )
			{
				p.isNegation = true; i++;
			}

			if(i-1>=0 && arr[i]=='~' && arr[i-1]=='~')
			{
				p.isNegation = false; i++; 
			}

			if(arr[i]>= 'A' && arr[i]<='Z')
			{
				StringBuilder s = new StringBuilder();
				while(i<n && arr[i]!='(')
				{
					s.append(arr[i]);
					i++;
				}

				p.predicateName = s.toString();

			}


			if(arr[i] =='(')
			{
				i++;
				while(arr[i]!=')')
				{
					while(arr[i]>= 'a' && arr[i]<='z')

					{
						String variable = new String();
						StringBuilder varName = new StringBuilder();
						while(i<n && arr[i]!=',' && arr[i]!=')')
						{
							varName.append(arr[i]);
							i++;
						}
						variable = varName.toString();
						argList.add(variable);

					}

					if(arr[i]==',')
					{
						i++;
						continue;
					}

					if(arr[i]==')')
					{
						predicateList.add(new Predicate(p.predicateName, argList,p.isNegation));
						p = new Predicate();
						continue;
					}

					while(arr[i]>='A' && arr[i]<='Z')
					{
						StringBuilder constantValue = new StringBuilder();
						while(i<n && arr[i]!=',' && arr[i]!=')')
						{
							constantValue.append(arr[i]);
							i++;
						}
						argList.add(constantValue.toString());
					}

					if(arr[i]==',')
					{
						i++;
						continue;
					}

					if(arr[i]==')')
					{
						predicateList.add(new Predicate(p.predicateName, argList,p.isNegation));
						p = new Predicate();
						continue;

					}

				}
			}

			if(i+1<n && arr[i+1]=='|')
			{
				i++;
				continue;
			}

			if(i+1<n && arr[i+1] == ')')
			{
				i++;
				continue;
			}

		}

		sentence = new Sentence(predicateList);

		return sentence;
	}

	static Sentence negateSentence(Sentence s) {

		boolean presentNegationValue = s.predicateList.get(0).isNegation;
		s.predicateList.get(0).isNegation = !presentNegationValue;
		return s;
	}
	
	Sentence resolveTwoSentencesHelper(ArrayList<Predicate> s1, ArrayList<Predicate> s2, int i1, int i2){
		Sentence resolvedSentence = new Sentence();

		for(int i=0; i < s1.size(); i++){
			if(i!= i1)
				resolvedSentence.addPredicate(s1.get(i));
		}
		for(int j=0; j< s2.size(); j++){
			if(j != i2)
				resolvedSentence.addPredicate(s2.get(j));
		}
		return resolvedSentence;
	}

	Sentence resolveTwoSentences(Sentence s1, Sentence s2){
		Sentence SentenceOne = new Sentence(s1);
		ArrayList<Predicate> predicateListofs1 = new ArrayList<>(SentenceOne.predicateList);
		Sentence SentenceTwo = new Sentence(s2);
		ArrayList<Predicate> predicateListofs2 = new ArrayList<>(SentenceTwo.predicateList);

		boolean equal = false;

		for(int i=0; i < predicateListofs1.size(); i++)
			for (int j = 0; j < predicateListofs2.size(); j++) {
				if (predicateListofs1.get(i).predicateName.equals(predicateListofs2.get(j).predicateName)
						&& ((predicateListofs1.get(i).isNegation != predicateListofs2.get(j).isNegation))) {
					equal = true;
					if (predicateListofs1.get(i).arguments.size() == predicateListofs2.get(j).arguments.size()) {
						for (int l = 0; l < predicateListofs1.get(i).arguments.size(); l++) {
							String argument1 = predicateListofs1.get(i).arguments.get(l);
							String argument2 = predicateListofs2.get(j).arguments.get(l);

							String unifiedName;
							if(Character.isLowerCase(argument1.charAt(0))){
								if(Character.isLowerCase(argument2.charAt(0)))
									unifiedName = argument1;
								else
									unifiedName = argument2;
							}
							else{
								if(Character.isLowerCase(argument2.charAt(0))){
									unifiedName = argument1;
								}
								else if (argument1.equals(argument2))
									unifiedName = argument1;
								else
									unifiedName = null;
							}

							if(unifiedName != null){
								if(!argument1.equals(unifiedName)){
									for(Predicate p:predicateListofs1){
										for(int m=0; m<p.arguments.size();m++){
											if(argument1.equals(p.arguments.get(m)))
												p.arguments.set(m,unifiedName);
										}
									}
									for(Predicate p:predicateListofs2){
										for(int m=0; m<p.arguments.size();m++){
											if(argument1.equals(p.arguments.get(m)))
												p.arguments.set(m,unifiedName);
										}
									}
									argument1 = unifiedName;
									argument2 = unifiedName;
								}

								if(!argument2.equals(unifiedName)){
									for(Predicate p:predicateListofs2){
										for(int m=0; m<p.arguments.size();m++){
											if(argument2.equals(p.arguments.get(m)))
												p.arguments.set(m,unifiedName);
										}
									}
									for(Predicate p:predicateListofs1){
										for(int m=0; m<p.arguments.size();m++){
											if(argument2.equals(p.arguments.get(m)))
												p.arguments.set(m,unifiedName);
										}
									}
									argument1 = unifiedName;
									argument2 = unifiedName;
								}
							}

							if (!argument1.equals(argument2))
								equal = false;
						}
					}
				}
				if (equal && (predicateListofs1.get(i).isNegation != predicateListofs2.get(j).isNegation))
					return resolveTwoSentencesHelper(predicateListofs1, predicateListofs2, i, j);
				equal = false;
			}
		return resolveTwoSentencesHelper(SentenceOne.predicateList, SentenceTwo.predicateList, -1, -1);
	}
	
	boolean firstOrderLogic(KnowledgeBase knowledgeBase, Sentence query){
		start = System.currentTimeMillis();
		ArrayList<Sentence> KBSentenceList = new ArrayList<>(knowledgeBase.sentenceList);

		KBSentenceList.add(query);

		ArrayList<Sentence> newSentencesToBeAdded = new ArrayList<>();

		while(((System.currentTimeMillis()-start)/1000) < maxTime){
			for(int i=0; i<KBSentenceList.size()-1 && (((System.currentTimeMillis()-start)/1000) < maxTime);i++){
				for(int j=i+1; j< KBSentenceList.size() && (((System.currentTimeMillis()-start)/1000) < maxTime); j++){
					Sentence result = resolveTwoSentences(KBSentenceList.get(i), KBSentenceList.get(j));
					if(result.predicateList.size() == 0)
						return true;
					//if(resolvent.predicates.size() < (Math.max(clauses.get(i).predicates.size(), clauses.get(j).predicates.size()))){
					if(result.predicateList.size() < (KBSentenceList.get(i).predicateList.size() + KBSentenceList.get(j).predicateList.size())){
						removeRepeatingPredicates(result);
						newSentencesToBeAdded.add(result);

					}
				}
			}

			ArrayList<Sentence> identifiedSentence = new ArrayList<>();

			for(int i=0; i<newSentencesToBeAdded.size() && (((System.currentTimeMillis()-start)/1000) < maxTime);i++){
				Sentence s = newSentencesToBeAdded.get(i);
				boolean newSentence = true;

				for(Sentence c : KBSentenceList){
					if (sentenceEqualityCheck(s,c)){
						newSentence = false;
					}
				}

				for(Sentence newIdentified: identifiedSentence){
					if(sentenceEqualityCheck(s,newIdentified)){
						newSentence = false;
					}
				}

				if (newSentence){
					identifiedSentence.add(s);
				}
			}
						
			if(((System.currentTimeMillis()-start)/1000) >= maxTime)
				return false;

			if (identifiedSentence.size() == 0){
				return false;
			}
			KBSentenceList.addAll(identifiedSentence);
		}
		return false;
	}

	
	public static void main(String[] args) throws IOException {
		String fileName = "input.txt";
		String line = null;
		File f = new File("output.txt");
		FileOutputStream fos;

		fos = new FileOutputStream(f);
		PrintWriter printwriter = new PrintWriter(fos);
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		line = bufferedReader.readLine().trim();
		int numQueries = Integer.parseInt(line);

		ArrayList<Sentence> queryList = new ArrayList<>();

		for(int i=0; i<numQueries;i++)
		{
			line = bufferedReader.readLine().trim();
			line = line.replaceAll("\\s+","");
			Sentence s = ConvertStringToSentence(line);
			queryList.add(s);
		}

		line = bufferedReader.readLine().trim();
		int numofSentences = Integer.parseInt(line);

		ArrayList<Sentence> KnowledgeBase = new ArrayList<>();

		for(int i=0; i<numofSentences;i++)
		{
			line = bufferedReader.readLine().trim();
			line = line.replaceAll("\\s+","");
			Sentence s = ConvertStringToSentence(line);
			KnowledgeBase.add(s);
		}


		bufferedReader.close();

		homework hw = new homework();

		for(Sentence s : queryList)
		{
			Sentence negatedQuery = negateSentence(s);
			KnowledgeBase KB = new KnowledgeBase(KnowledgeBase);
			boolean result = false;
			try{
				result = hw.firstOrderLogic(KB, negatedQuery);
			} 
			catch(Exception e)
			{
				result = false;
			}
			if(result)
				printwriter.write("TRUE");
			else
				printwriter.write("FALSE");
			printwriter.write("\n");
		}
		printwriter.flush();
		fos.close();
		printwriter.close();
	}

}

