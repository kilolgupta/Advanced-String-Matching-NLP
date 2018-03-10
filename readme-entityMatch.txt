I have pre-processed the words before calculating the matching score. Various word preprocessings done are as follows:
1. All Words were converted to the same lower case
2. We removed the common words like the following:"mr", "miss", "ms", "mrs", "dr", "phd", "prof", "jr",
            "sr", "rev", "corp", "corporation", "comp", "company", "llc", "llp", "inc", "incorp", "incorporation", "org",
            "organisation", "organization", "association"   before starting the comparison. This is because Mr. as a prefix doesn't actually contribute to the identity while comparing two persons or organisations.
3. Only for persons' lists, if the names had brackets around them, then we compares the names with the word inside the bracket and without it. For eg. If one of the entries in the input lists was Barack (Hossein) Obama, we compared the entries in one file with Barack Obama, and Barack Hossein Obama and considered the highest matching score out of the two.
4. If the names (type P- Persons) were of the following format- "Last Name, First Name", we preprocessed it to convert it to "First Name Last Name" for the sake of having same format while comparing the two.
5. We removed any punctuation from the words after doing the above pre-processing

-----------------------------------------------------------------------------------------------------------------------------------------

I have implemented the comparison logic as per the following guidelines, let there be two words: source and target that we are trying to match:

1. If there is an exact match between source and target, score is 100%, 
     for eg. Barack Obama and Obama, Barack 

2. If there is a complete substring (disjoint or contiguous), score is 100%, 
     for eg. Barack Hussein Obama and Obama, Barack

3. If some words match, score is (matchCount*2)/(sourceLength + TargetLength), 
     for eg. Columbia University College and Columbia College in New York, score = (2*2)/(3+5) *100 = 50%
   We aren't considering abbreviated first or last names like in P. Walter, P. is not considered while finding the matching words

4. If there are no matching words, we calculate the editDistance using Levenshtein algorithm and score is (threshold-editDistance)/threshold, where threshold = (substitution_cost*smaller_length+insertion_cost*length_difference)
     for eg. New York and Sunny California
     I have kept the costs as follows-
     insertionCost = DeletionCost = 1.0
     substitutionCost = 2.0

---------------------------------------------------------------------------------------------------------------------------------------------

The format of the output file is pairs of matched words separated by a pipe (|) delimiter, this delimiter can be easily changed using a global variable set at the beginning.

Enter the names of the two files you want to match- 
persons1.txt
persons2.txt
Enter the type of data- P(Persons), O(Organisations), L(Locations)
P
Enter the threshold percentage beyond which 2 words match: 
80

For now, I have only outputted the matched pairs, and not their matching score (in percentage). This can be changed if required. In the sample data that we tried our program on, we had roughly 16000 rows in persons.1txt and 41000 rows in persons2.txt, so the program took around 1.5 hours to generate 11000 matched pairs.