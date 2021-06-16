# cs1653-viginere-btd27
Assignment 1 for Dr. Khattab's CS1653
TA: victorzhz

My method of approach for cracking this cipher was simple enough.  Dr. Khattab had said that it was one of the primitive forms of cryptography mentioned in Lecture 4, so I started there and eliminated possibilities by possible application.  Rail Fence ciphers were immediately out, as was scytale and steganography due to obvious physical means.  I tried using an Atbash cipher, as well as a Caesar cipher knowing that FU1653 mapped to CS1653, so I narrowed it down to an educated guess that this cipher was a Viginere cipher due to substitution and multiple different shifts.

From there, I started to write some java to solve this problem, however I ended up just writing some java to help me with the frequency analysis portion.  In my not-so-accurately-named Kasiski.java, I read in the ciphertext (that I edited to remove whitespace and put each "word" from the ciphertext on a new line).  I then created a list which I read in substrings between 3 < L < 10 chars long.  My reasoning here was that any cipher key less than 3 chars was ineffective, and I also assumed that Dr. Khattab wouldn't give us something unreasonably difficult, so by cutting out keys less <2 and >10 chars, I eliminated a lot of the workload for my program.

Next, I created another list in which I stored all of the substrings that occurred >= 2 times + the frequency with which they occurred, which I also wrote to a text file.  I was then able to use this text file to manually sort through, collect the substrings with the highest frequencies (on page 2 of the pdf), then count by hand the distances between each occurrence of each substring.  From there, I broke down the distances into their respective factors, not going above 20 for the factor length just for ease of work.  From this list, it was pretty clear that the key length was going to be 5 characters.

From this point, I used some of the known mappings from the ciphertext.  FU1653 obviously mapped to CS1653, so I knew FU -> DC in consecutive order (which I figured out using the ABC x ABC table from Lecture 4, or page 3 of my pdf).  Additionally, the "ddq123 rphts vjf123 kg twxt" from the ciphertext was most likely going to be "abc123 where abc123 is you" because every professor that teaches a CS course has their students submit their assignments this way, and always uses this format on their assignments.  From this point, I simply did a few runs of trial & error until I got the matches that worked, revealing the key to be COVID, and then I decrypted the entire message.

******* CIPHERTEXT *******
Eci ouch ptdvwjvv Dm ymftmkblpu oplu azavcuz grw vvdh echxoghzl vvsk wqg ca bkkg
vavkuiuhph Ow fqakthvs oph pssb vvsk wi vvz ivuwbvpgbo grw apaw uiwulv hcm
fqrz wi vvz brqzn grw inmg vc ymftmkb wjwn uhugvoh kb v Olvvpj ugdjalvcmg
Bqi pahf o Fivkgfq hzohqqchdwq cby bkgb v juwhz nrtqz wq czg shag jn
wjs yqvecqmugr gmqihc a Vwphqw vvz krfs twx wgzl iqf wwwj ca bkggz awgdn br c
dmqychz Olvvpj ugdjalvcmg qcazl fu1653 jdohpsmm ddq123 rphts vjf123 kg twxt
Ddbw wgzzqcaz ahromiwg hcm zqfya zkhc lduvza Vjomm wjwn zhrcnqwqft elvv oph VO qqfvcmhkb Wa grw gjtygr nbhr
cim xuwio hphdzhnm kmq cby xdrsm afcb twxt vvvgyfdbwgb ywfwazvw cby axdadb lv
Wi mlvvzz fcgz axdadb d tsvlpg tdth fsnkukpdvj acpz drdmwdev Dn bqi ciyg zdbwns
ywfwazvwchdwq cpjcw acpz vqzpblqb twx oot kkqcnm wq whxogazvw c pvale Yvaluyd
macadvdvwjv wq siaxts opdv mjc jgh acon djqqvg jv wjwn awgd jn wjs vavkuiuhph

******* DECRYPTED PLAIN TEXT ******* (with interpretive punctuation by me)
CONGRATULATIONS! BY DECRYPTING THIS MESSAGE, YOU HAVE COMPLETED STEP ONE OF THIS ASSIGNMENT.  TO COMPLETE THE NEXT STEP OF THE ASSIGNMENT, YOU MUST SUBMIT THE CODE OF THE TOOLS YOU USED TO DECRYPT THIS MESSAGE IN A GITHUB REPOSITORY.

YOU USED A KASISKI EXAMINATION AND THEN A BRUTE FORCE ON ALL KEYS OF THE DISCOVERED LENGTH S.  SUBMIT THE CODE YOU USED FOR BOTH OF THESE STEPS TO A PRIVATE GITHUB REPOSITORY NAMED CS1653 VIGINERE ABC123 WHERE ABC123 IS YOUR PITT USERNAME.  SEPARATE THE WORDS WITH DASHES.  SHARE THIS REPOSITORY WITH THE TA VICTORZHZ.  

IF YOU SOLVED STEP ONE USING ENTIRELY PEN AND PAPER, SCAN YOUR HANDWRITTEN DOCUMENT AND SUBMIT IT.  IN EITHER CASE, SUBMIT A README FILE DESCRIBING YOUR APPROACH.  IF YOU HAVE LITTLE DOCUMENTATION ABOUT YOUR SOLUTION, YOU MAY CHOOSE TO IMPLEMENT A BASIC KASISKI EXAMINATION TO ENSURE THAT YOU GET FULL POINTS ON THIS STEP OF THE ASSIGNMENT.
