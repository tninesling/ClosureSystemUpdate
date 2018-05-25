	program implication11A

!!This is the main program for finding equivalences and implications.
!!For columns X and Y, the implications are counted as follows following.
    !! 0 0 -> 0   The implication is true but by default.
    !! 0 1 -> 0   The implication is true but by default.
    !! 1 0 -> -1  The implication fails: add 1 to the deficit
    !! 1 1 -> 1   The implication holds: add 1 to the support
	integer mesa(2000,2000)         !!input matrix; rows=samples,cols=genes
	integer order(2000,2000)        !!order on columns
	integer loose(2000,2000)        !!loose order on columns
!![jb] this is a modified order that has been added
	integer support, deficit, diff  !!diff = support - deficit
	integer nogenes                 !!number of genes
!![jb] there are two columns for each gene:  high expression and low expression.
!! Also columns for normal and tumor samples.  So n = #cols = 2*nogenes+2
!! Making it a variable allows for different classifications of samples in future.
	integer interp(2000)            !!interprets n+k as -k for output
	integer equiv(2000,2000)        !!equiv(i,j)=1 if col i = col j
	integer aeq(2000,200), numaeq(2000), done(2000)
!! for an equivalence class of columns [i j2 j3 ...] we think of the first 
!! element "i" as the representative of the class.  The array "aeq" lists
!! the class:  aeq(i,1)=i  aeq(i,2)= j2 etc.  Then numaeq(i) gives the number
!! of cols in the class.  Meanwhile, done(j1) = done(j2) = ... = 1 to indicate
!! that they are already in a class, but not the representative (the first one).
	integer overlap(202)   !!indicates those rows where two cols are both 1
	integer overdef(202)   !!indicates those rows where the deficit occurs
	integer mrtest(2000), meetand(202)
!![jb] these are used in the reducibility test - explain later
	integer supptol, deftol
!![jb] the amount of support required, and deficit allowed, to accept
!! an implication as true.  These are crucial parameters.
	integer dsupptol, ddeftol
!![jb] same for ternary implications
	character(3) reflect    !!whether or not to use +k and -k
	character(3) targetlim  !!whether or not to limit the target
	integer limlow, limhigh !!target limits
!!The following are for sorting the binary output by support or diff
	integer nobinimps, maxnobinimps, maxbinsupp, maxbindiff
	integer binrecord(10000,5), vlap(10000,202), vdef(10000,202)
!!The following are for sorting the ternary output by support or diff
	integer noterimps, maxnoterimps, maxtersupp, maxterdiff
	integer terrecord(10000,6)

  65    format(i4)   !! some 4-byte integer
  66    format(40i2) !! 40 2-byte integer
  67    format(i5,' ->',i5,' sup/def/dif ',3i3) !! Format for binary
						!! implication output
 167	format(i5,' ->',i5)  !! short form for binary implication output
  68    format(2i5,' ->',i5,' sup/def/dif ',3i3) !! Format for ternary
						!! implication output
  70    format(i4,' equivalent ') !! string containg 4-byte integer 
	   !! (the number that are equivalent) and the word "equivalent"
  71	format('equiv cols ',15i5) !! string listing the cols that are equivalent
 170	format(i4,' loose equivalent ')     !!same for loose equivalence
 171	format('loose equiv cols ',15i5)    !!same for loose equivalence
					    !! more output strings
  72	format('density of matrix ',2i6,f9.4)
  73	format(4x,'reducible column ',i5)
  74	format('meetands ',16i5)
  75    format('sup on samples ',16i5)
  76    format(4x,'LOOSE SUMMARY ')
  77    format( 'def on samples  ',16i5)

   	 open(unit=7,file='imply.in') 
!! file-descriptor 7 is referring to input file
    	open(unit=8,file='imply.out') 
!! file-descriptor 8 is referring to output file

    !!input the matrix
    	read(7,*)m   !!no of rows from file -> call it m
    	read(7,*)n   !!no of cols from file -> call it n

    !! [ulno] read input matrix
    	do i=1,m
    	read(7,*) (mesa(i,j), j=1,n)
    	enddo

    !!  whether or not to use +k and -k for high/low expression
	print *, "Do you want to use +k and -k? (yes or no)"
	read *, reflect
	if (reflect.eq."no") then
          do j=1,n
          interp(j)=j
          enddo
	endif

	if (reflect.eq."yes") then
	!! Columns 1 to nogenes stand for high expression of the gene;
	!! columns nogenes+1 to 2*nogenes are for low expression;
	!! the last two columns are normal (n-1) and tumor(n) status.	
	  nogenes=(n-2)/2
	  !!interpret column "n+k" as "-k" for output purposes
	  do i=1,n
	  interp(i)=i
	  if ((nogenes.lt.i).and.(i.le.2*nogenes)) interp(i)=-i+nogenes
	  enddo
	endif

    !!  whether or not to limit the target
	print *, "Do you want to limit the target for ternaries? (yes or no)"
	read *, targetlim
	if (targetlim.eq."no") then
          limlow=1
          limhigh=n
	endif

	if (targetlim.eq."yes") then
	print *, "Enter the lower and upper target limits"
	read *, limlow, limhigh
	endif

	!!input the threshold and tolerances
	print *,'input support threshold and deficit tolerance'
	read *, supptol, deftol
!! [ulno] read from keyboard threshold (supptol) and deficit tolerance (deftol)
	print *,'input threshold and tolerance for X&Y->Z'
	read *, dsupptol, ddeftol
!! [ulno] read from keyboard thres and tol for ternary implications
!! [jb] none of these four is a very good variable name

!![jb]  Write relevant information to imply.out (and blanks to be filled)
	write(8,401)
	write(8,402)
	write(8,403)
	write(8,404)
	write(8,405)
	write(8,406)
	write(8,407)
  401	format('Fill in the relevant information on this test')
  402	format('Date: ')
  403	format('Input file: ')
  404	format('Source: ')
  405	format('Centering: ')
  406	format('Normalization: ')
  407	format('Stripping: ')
	write(8,408) m
	write(8,409) n
	write(8,410) supptol
	write(8,411) deftol
	write(8,412) dsupptol
	write(8,413) ddeftol
	write(8,414) limlow, limhigh
  408	format('No of rows: ',i5)
  409	format('No of cols: ',i5)
  410	format('Support threshold: ',i5)
  411	format('Deficit tolerance: ',i5)
  412	format('Support threshold for ternary: ',i5)
  413	format('Deficit tolerance for ternary: ',i5)
  414	format('Limits on target: ',2i5)

	!!compute density of the data matrix 
	kt=0
	do i=1,m
	do j=1,n
	kt=kt+mesa(i,j)
	enddo
	enddo
	density = real(kt)/real(m*n)
	write(8,72) kt, m*n, density

	write(8,415)
	write(8,416)
	write(8,417)
  415	format(' ')
  416	format('RESULTS')
  417	format('EQUIVALENCE')

!!date, input file, source, centering, stripping, rows, cols
!!suptol, deftol, dsuptol, ddeftol, density

!!!!!!!!!!!!!!!!!!!!!!!! SETUP PAU !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	!equivalent
	!!equiv(i,j) is the equivalence relation 
	!!aeq(i,j) lists the classes, with aeq(i,1) being the class rep
        !!numaeq is size of class, done=1 for non-reps of class

        !! initialize doneaeq to 0 and equiv, loose  and order to identity
	  do i=1,n
	  done(i)=0
	  do j=1,n
	  equiv(i,j)=0
	  order(i,j)=0
	  loose(i,j)=0
	  enddo
	  equiv(i,i)=1   
	  order(i,i)=1
	  loose(i,i)=1
	  enddo

	do 99 i=1,n-1
!! [ulno] go in i through all columns apart the last
	if (done(i).eq.1) goto 99   
!! means this col is non-rep in earlier class
!! [ulno] if done, continue [jb] with next i
!! record in the vector "overlap" which rows have mesa(k,i)=1
!! the overlap vector will be used as output

	  support=0              !!counts no of ones in the column

	  do k=1,m
	  if (mesa(k,i).eq.1) then
  	    support=support+1
	    overlap(support)=k        !!tracks rows the ones are in
          endif
	  enddo
	numaeq(i)=1            !! so far col "i" is equivalent only to itself
	aeq(i,1)=i             !! "i" is the first thing in its class

	do 98 j=i+1,n          !! now find cols "j" with the same pattern
	do k=1,m               !! compare the columns in each row "k"
	if (mesa(k,i).ne.mesa(k,j)) goto 98
	enddo
    !! [jb] if the columns are different abort

    !!if here then col j is equivalent to col i 
 	numaeq(i)=numaeq(i)+1     !! found another equivalent row
    	aeq(i,numaeq(i))=j        !! add row to list
	done(j)=1                 !! j is not the class rep

	!! Now record the equivalences (to col i) in the arrays
        !! equiv, order and loose

	  do kk=1,numaeq(i)
	     equiv(aeq(i,kk),j)=1
	     equiv(j,aeq(i,kk))=1
	     order(aeq(i,kk),j)=1
	     order(j,aeq(i,kk))=1
	     loose(aeq(i,kk),j)=1
	     loose(j,aeq(i,kk))=1
	  enddo

  98	continue

	!!print the nontrivial classes to the output file:
	!!the number equivalent, their col numbers, # of ones, their positions
	if (numaeq(i).gt.1) then
	write(8,70)numaeq(i)
	write(8,71)(interp(aeq(i,k)),k=1,numaeq(i))
	write(8,75)(overlap(k),k=1,support)
	endif

  99	continue

!! the last column was not included in the loop, so fix that
	if (done(n).ne.1) then 
	  numaeq(n)=1      
	  aeq(n,1)=n
	endif

!!!!!!!!!!!END OF EQUIVALENCE!!!!!!!!!!!!!!!!!!!!!!!!!!

	write(8,715)
	write(8,716)
  715	format(' ')
  716	format('BINARY')

	!!order = binary implications X -> Y
	!!this section finds the binary implications with support at least
        !!supptol and deficit at most deftol
	nobinimps=0
	maxnobinimps=10000   !!array size can be adjusted
	maxbinsupp=0
	maxbindiff=0

	!!looking for i -> j
	do 102 j=1,n
	if (done(j).eq.1) goto 102   !!only use the reps of equiv classes
	do 101 i=1,n
	if (done(i).eq.1) goto 101   !!only use the reps of equiv classes
	if (i.eq.j) goto 101            !!and i ne j
	support=0
	deficit = 0
	diff = 0                        !!diff = support - deficit
	do k=1,m
	if (mesa(k,i).eq.1) then
	  if (mesa(k,j).eq.1) then
             support=support+1
	     overlap(support)=k         !!keeps track of samples for support
	  endif
	  if (mesa(k,j).eq.0) then
	      deficit=deficit+1
	      overdef(deficit)=k	!!keeps track of rows for deficit
	  endif
	endif
	enddo
	diff=support-deficit
	if ((support.ge.1).and.(deficit.eq.0)) then
		order(i,j)=1
		do ii=1,numaeq(i)
		i3=aeq(i,ii)
		do jj=1,numaeq(j)
		j3=aeq(j,jj)
		order(i3,j3)=1
		enddo
		enddo
	!!else
		!!order(i,j)=0
	endif
	if ((support.ge.supptol).and.
     1     (equiv(i,j).eq.0).and.(deficit.le.deftol)) then
	   loose(i,j)=1
	!!record for future output
	nobinimps=nobinimps+1
	binrecord(nobinimps,1)=interp(i)
	binrecord(nobinimps,2)=interp(j)
	binrecord(nobinimps,3)=support
	binrecord(nobinimps,4)=deficit
	binrecord(nobinimps,5)=diff
	if (support.gt.maxbinsupp) maxbinsupp=support
	if (diff.gt.maxbindiff) maxbindiff=diff
	do k=1,support
	vlap(nobinimps,k)=overlap(k)
	enddo
	do k=1,deficit
	vdef(nobinimps,k)=overdef(k)
	enddo
	if (nobinimps.eq.maxnobinimps) then
	  print *, 'RUNNING OUT OF ROOM'
	  write(8,*) 'ADJUST RECORD SIZE UP'
	  goto 500
 	endif
	endif
 101	continue
 102	continue

	!!now output binary imps but sorted by support
 500	continue
	do 501 mm=maxbinsupp,supptol,-1
!!	do 501 mm=maxbindiff,supptol-deftol,-1 !!could sort by diff
	do 502 kk=1,nobinimps
	if (binrecord(kk,3).eq.mm) then
	   write(8,67) (binrecord(kk,j),j=1,5)
           write(8,75)(vlap(kk,j),j=1,binrecord(kk,3))
	   write(8,77)(vdef(kk,j),j=1,binrecord(kk,4))
	!!REFINEMENT CHECK!!
	   ii=binrecord(kk,1)
	   jj=binrecord(kk,2)
	   do 522 kp=1,nobinimps
	   if (kp.eq.kk) goto 522
	   ip=binrecord(kp,1)
	   jp=binrecord(kp,2)
	   if ((order(ii,ip).eq.1).and.(order(jp,jj).eq.1)) then
            write (8,520) (binrecord(kp,j),j=1,5)
 520	    format('REFINED BY ',i4,' -> ',i4, '  s/d/d ',3i4)
	   endif
 522	   continue
	!! END REFINEMENT CHECK !!
	endif
 502	continue
 501	continue

!!!!!!!!!!!!!!END OF ORDER AND LOOSE ORDER SECTION!!!!!!!!!!!!!!!!!!!!

	write(8,717)
	write(8,718)
  717	format(' ')
  718	format('MEETS')

	!!meet reducibles 
        !!this section finds the reducible elements X & Y = Z (or more)
	!!the language here uses "meets" but it can also be done dually

	do 202 j=1,n
	if (done(j).eq.1) goto 202
	mkt=0  		                   !!meetand count
	do k=1,m
	mrtest(k)=1
	enddo

	!!mrtest will be a column vector whose entries are the product
	!!(= meet) of all the columns strictly bigger than column "j".
	!!if mrtest = column j, then column j is reducible; otherwise not.
	!!Generally, we use irreducibles in our implications

	do 201 i=1,n
	if (done(i).eq.1) goto 201      !!only one from each equiv class
	if (order(j,i).eq.0) goto 201
	if (i.eq.j) goto 201
	!! if here j < i so take meet
	mkt=mkt+1
	meetand(mkt)=i
	do k=1,m
	mrtest(k)=mrtest(k)*mesa(k,i)
	enddo
 201	continue
	!!now test if meet came down to j
	do k=1,m
	if (mrtest(k).ne.mesa(k,j)) goto 202
	enddo
	!!if here j is meet reducible
	done(j)=1             !!so col "j" won't be considered next section
	write(8,73) j
	write(8,74) (interp(meetand(k)),k=1,mkt)  !!print out this information
 202	continue
	!!preceding section does not attempt to write column j irredundantly
        !!as a meet.  It would be useful to add this information later

!!!!!!!!!!!END OF REDUCIBILITY TEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	write(8,717)
	write(8,719)
  719	format('LOOSE')

	write(8,76)

	!now redo everything with the loose order!!
	do 199 i=1,n-1
	if (done(i).eq.1) goto 199   !!means this col is non-rep in earlier class
	numaeq(i)=1
	aeq(i,1)=i                     !!"i" is the first thing in its class
	do 198 j=i+1,n                 !!now find pairs with i->j and j->i
	if ((equiv(i,j).eq.1).or.((loose(i,j).eq.1).and.(loose(j,i).eq.1))) then
	  numaeq(i)=numaeq(i)+1
	  aeq(i,numaeq(i))=j
	  done(j)=1                   !!j is not the class rep
	  do kk=1,numaeq(i)
	     equiv(aeq(i,kk),j)=1
	     equiv(j,aeq(i,kk))=1
	     loose(aeq(i,kk),j)=1
	     loose(j,aeq(i,kk))=1
	  enddo
	endif
 198	continue
	!!print the nontrivial classes to the output file:
	!!the number loose equivalent, their col numbers
	if (numaeq(i).gt.1) then
	write(8,170)numaeq(i)
	write(8,171)(interp(aeq(i,k)),k=1,numaeq(i))
	endif
 199	continue
	
!!!!!!!!!!!!!!END OF LOOSE EQUIVALENCE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	!!order = binary implications X -> Y

	!!looking for i -> j
	do 302 j=1,n
	if (done(j).eq.1) goto 302   !!only use the reps of equiv classes
	do 301 i=1,n
	if (done(i).eq.1) goto 301   !!only use the reps of equiv classes
	if (equiv(i,j).eq.1) goto 301            !!and i ne j
	if (loose(i,j).eq.1) then
	   write(8,167)interp(i),interp(j)
	endif
 301	continue
 302	continue

	!!Reducibles using loose will require thought

!!!!!!!!!!!!!!END OF LOOSE ORDER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	write(8,717)
	write(8,720)
  720	format('TERNARY')


	!!ternary implications X & Y -> Z
	!!this section finds the ternary implications with support at least
        !!dsupptol and deficit at most ddeftol
	!!eventually supptol&dsupptol, deftol&ddeftol should be linked
        !!as a single parameter
	noterimps=0
	maxnoterimps=10000   !!array size can be adjusted
	maxtersupp=0
	maxterdiff=0


	!!looking for i & ii -> j
	do 33 j=limlow, limhigh      !!limits were input
	if ((targetlim.eq."no").and.(done(j).eq.1)) goto 33
	!!if the target is not specified, do only the first one from each loose
        !!equivalence class; if not, then do the targets requested
	do 32 i=1,n-1
	if (done(i).eq.1) goto 32
	if (loose(i,j).eq.1) goto 32   !!skip if i -> j already
	do 31 ii=i+1,n
	if (done(ii).eq.1) goto 31
	if (loose(ii,j).eq.1) goto 31   !!skip if ii -> j already
	support=0
	deficit = 0
	diff = 0
	do k=1,m
	if ((mesa(k,i).eq.1).and.(mesa(k,ii).eq.1)) then
	  if (mesa(k,j).eq.1) then
             support=support+1
	     overlap(support)=k
	  endif
	  if (mesa(k,j).eq.0) then
	      deficit=deficit+1
	      overdef(deficit)=k
	  endif
	endif
	enddo
	diff=support-deficit
	if ((support.ge.dsupptol).and.(deficit.le.ddeftol)) then
	!!record for future output
	noterimps=noterimps+1
	terrecord(noterimps,1)=interp(i)
	terrecord(noterimps,2)=interp(ii)
	terrecord(noterimps,3)=interp(j)
	terrecord(noterimps,4)=support
	terrecord(noterimps,5)=deficit
	terrecord(noterimps,6)=diff
	if (support.gt.maxtersupp) maxtersupp=support
	if (diff.gt.maxterdiff) maxterdiff=diff
	do k=1,support
	vlap(noterimps,k)=overlap(k)
	enddo
	do k=1,deficit
	vdef(noterimps,k)=overdef(k)
	enddo
	if (noterimps.eq.maxnoterimps) then
	  print *, 'RUNNING OUT OF ROOM'
	  write(8,*) 'ADJUST RECORD SIZE UP'
	  goto 600
 	endif
	endif
  31	continue
  32	continue
  33	continue

	!!now output ternary imps but sorted by support
 600	continue
	do 601 mm=maxtersupp,supptol,-1
!!	do 601 mm=maxterdiff,supptol-deftol,-1 !!could sort by diff
	do 602 kk=1,noterimps
	if (terrecord(kk,4).eq.mm) then
	   write(8,68) (terrecord(kk,j),j=1,6)
           write(8,75)(vlap(kk,j),j=1,terrecord(kk,4))
	   write(8,77)(vdef(kk,j),j=1,terrecord(kk,5))
	!!REFINEMENT CHECK!!
	   ii1=terrecord(kk,1)
	   ii2=terrecord(kk,2)
	   jjj=terrecord(kk,3)
	   do 622 kp=1,noterimps
	   if (kp.eq.kk) goto 622
	   ip1=terrecord(kp,1)
	   ip2=terrecord(kp,2)
	   jjp=terrecord(kp,3)
	   if ((order(ii1,ip1).eq.1).and.(order(ii2,ip2).eq.1).and.
     1             (order(jjp,jjj).eq.1)) then
            write (8,620) (terrecord(kp,j),j=1,6)
 620	    format('REFINED BY ',2i4,' -> ',i4, '  s/d/d ',3i4)
	   endif
	   !! in switched order !!
	   if ((order(ii2,ip1).eq.1).and.(order(ii1,ip2).eq.1).and.
     1             (order(jjp,jjj).eq.1)) then
            write (8,620) (terrecord(kp,j),j=1,6)
	   endif
 622	   continue
	!! END REFINEMENT CHECK !!
	endif
 602	continue
 601	continue

!!!!!!!!!!!!!!END OF ORDER AND LOOSE ORDER SECTION!!!!!!!!!!!!!!!!!!!!

	!!compute density of the data matrix
	!!kt=0
	!!do i=1,m
	!!do j=1,n
	!!kt=kt+mesa(i,j)
	!!enddo
	!!enddo
	!!density = real(kt)/real(m*n)
	!!write(8,72) kt, m*n, density

	return
	end

