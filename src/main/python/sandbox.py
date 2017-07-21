'''
Created on Nov 25, 2010

@author: Reuven

This will use rsets for the element of the closure system.

Suppose you have S={1,2,3,4,5,6}, and the family of subsets of S:
F={emptyset,1,12,13,136,1362,4,45,134,1346,13456,123456}

'''
import random
import collections

class rset(frozenset):

    def __str__(self):
        return str(tuple(self))

    def __repr__(self):
        return str(tuple(self))

class imp(tuple):

    def left(self):
        return self[0]

    def right(self):
        return self[1]

    def then(self):
        if not len(list(self[1]))==0:
            return list(self[1])[0]
        return 0

    def __str__(self):
        #return str(self[0])+","+str(self[1])
        s=""
        if len(self[0])==0:
            s+="{}"
        for i in self[0]:
            s+=str(i)+" "
        s+="-> "
        for i in self[1]:
            s+=str(i)+" "
        if len(self[1])==0:
            s+="{}" # should be chr(216)
        return s

    def __repr__(self):
        return str(self)

    #turn a list of lists into a rset of rsets
def freeze(li):
    s = set()
    for i in li:
        s.add(rset(i))
    return rset(s)

def subfreeze(li):
    s = []
    for i in li:
        s += [rset(i)]
    return s


def value(a):
    return len(a[0])*1000 + len(a[1])*100+list(a[0])[0]*10+list(a[1])[0]


def list_powerset(lst):
    # the power set of the empty set has one element, the empty set
    result = [[]]
    for x in lst:
        result.extend([subset + [x] for subset in result])
    return result

def powerset(domain):
    return rset(map(rset, list_powerset(list(domain))))

def generate(size=5, subs=5):
    S = rset(range(1,size+1))
    PS = powerset(S)
    subsets = rset(random.sample(PS, subs))
    return closure(subsets, S, PS)

def closure(subsets, S, PS):
    closed = set()
    for i in powerset(subsets) - rset([rset()]):
        closed.add(reduce(rset.intersection, list(i)))
    closed.add(rset())
    closed.add(S)
    return S, PS, rset(closed)

def standardize(S, PS, closed):
    cls = dict(createmap(S, PS, closed))
    closed = set(closed)
    closed2 = set(closed)
    for x in S:
        for y in S:
            if (cls[rset({x})]|rset({x}))==(cls[rset({y})]|rset({y})) and y>x: #if two elements share a closure, remove the larger
                #print str(x)+": "+ str(cls[rset({x})])+" and "+str(y)+": "+ str(cls[rset({y})])
                S = S - {y}
                for A in closed:
                    closed2.remove(A)
                    closed2.add(A - {y})
                closed = set(closed2)
    #Let V = {u in U : m({u}) / {u} is closed}
    cls = dict(createmap(S, PS, closed))
    for x in S:
        if not cls[rset({x})] in closed:
            #print str(x)+": "+str(cls[rset({x})])+" is not closed."
            S = S - {x}
            for A in closed:
                closed2.remove(A)
                closed2.add(A - {x})
            closed = set(closed2)
    PS = powerset(S)
    return S, PS, rset(closed)



def createmap(S, PS, closed):
    cls = []
    for i in PS:
        to = S
        for j in closed:
            if i <= j:
                to = to.intersection(j)
        cls.append(imp([i,to-i]))
    return cls

def critical(X, closed, cls):
    #a) X does not belong to F
    if X in closed:
        return False
    #b) if Y is a closure of X in F, then X is the minimal among all subsets whose closure is Y
    Y = cls[X] | X
    subsets = powerset(X) - rset([X])
    for i in subsets:
        if cls[i]|i == Y and quasiClosed(i, closed):
            return False
    #c) when adding X to F one gets again a closure system
    return quasiClosed(X, closed)

def quasiClosed(X, closed):
    #X is quasi-closed if for all closed sets Y, X intersection Y is closed.
    for Y in closed:
        if Y & X not in closed and Y&X != X:
            return False
    return True

def deriveClosure(S, PS, basis): #returns number of Iterations
    results = []
    maxIts = 0
    for i in PS:
        conclusion = i
        length = 0
        iterations =-1
        while len(conclusion) > length:
            iterations +=1
            length = len(conclusion)
            for j in basis:
                if j[0] <= conclusion:
                    product = conclusion | j[1]
        results.append(imp([i, rset(conclusion) - i]))
        maxIts = max(maxIts, iterations)
    return maxIts #return results to see closure (always same as original assuming a valid basis)

def deriveImplications(basis, premise): #returns number of Operations
    operations = 0
    conclusion = premise
    length = 0
    while len(conclusion) > length:
        length = len(conclusion)
        for j in basis:
            operations +=1
            if j[0] <= conclusion:
                conclusion = conclusion | j[1]
    return conclusion#, operations

def dDerive(dbasis, premise): #returns exact number of Operations. Only one run of DBasis.
    conclusions = [False]*12 #Simulating a length 12 Java boolean array
    ops = 1
    for i in premise:
        conclusions[i]=True
        ops+=1

    for j in dbasis:
        contained = True
        ops+=1
        for p in j[0]:
            if conclusions[p]==False:
                contained=False
                ops+=1
            ops+=1
        if contained:
            conclusions[j.then()]=True
            ops+=1
        ops+=1
    return conclusions, ops

def chainResolve(basis, premise):

    conclusions = [False]*12 #Simulating a length 12 Java boolean array
    ops=1
    for i in premise:
        conclusions[i]=True
        ops+=1
    clauselist =[[] for i in range(12)]
    propositions = [0]*len(basis) #This could be counted as len(basis) operations, but I'll assume we have an upper bound
    consequent = [0]*len(basis) #See above
    #Queue begins by containing the premise
    queue = list(premise)
    ops+=4
    #Clauselist: For each p, list clauses in which p appears
    for i in range(len(basis)):
        for x in basis[i][0]:
            clauselist[x].append(i)
            ops+=1
    #Propositions: Number of remaining (negative) literals in LHS of each clause
        for x in basis[i][0]:
            propositions[i]+=1
            ops+=1
    #Consequent: Positive literal of a given statement ('0' if none exists)
        consequent[i] = basis[i].then()
        ops+=1
    '''
    print clauselist
    print propositions
    print consequent
    print queue
    print "Pre-processing operations: "+str(ops)
    '''

    while len(queue)>0:
        p = queue.pop()
        ops+=1
        for index in clauselist[p]:
            propositions[index]-= 1
            ops+=1
            if propositions[index] == 0 and not conclusions[consequent[index]]:
                queue.append(consequent[index])
                conclusions[consequent[index]]=True
                #print str(consequent[index]) + " is true"
                ops+=2
            ops+=1
    return conclusions, ops

def orderable(ubasis):

    ubasis = set(ubasis)
    orderList = []
    for A in ubasis:
        for B in ubasis:
            if not B[1]<=(A[0]|A[1]) and not B[1] < deriveImplications(ubasis-{A}, (B[0]-A[1])|A[0]):
                orderList.append(imp([A,B]))

    #Check orderList for Cycles
    for A in orderList:
        followers = [A[0]]
        change = True
        while(change):
            change = False
            for B in orderList:
                if B[0] in followers and B[1] == A[0]: #We added A[0] to followers - a cycle:
                    #print "All Orders: "+str(orderList)
                    #print "Specific Cycle: "+str(followers)
                    return False, str(followers)
                if B[0] in followers and not B[1] in followers:
                    change = True
                    followers.append(B[1])
    return True, ""

def transposeOrderable(S, PS, basis, flips = []):
    #flips.append(basis)
    for premise in PS:
        conclusion = premise
        used = set()
        for A in basis:
                if A[0] <= conclusion and not A[1] <= conclusion:
                    conclusion = conclusion | A[1]
                    used.add(A)
        for B in basis:
            if B[0] <= conclusion and not B[1] <= conclusion:
                for A in used:
                    if A[1] < B[0]:
                        if (A,B) in flips:
                            print "Not Transpose Orderable"
                            print flips
                            return False
                        flips.append((A,B))
                        b2 = list(basis)
                        b2[basis.index(A)] = B
                        b2[basis.index(B)] = A
                        return transposeOrderable(S, PS, b2, flips)
    return True#, basis

def joinIrreducible(closed):
    jI = []
    for i in closed:
        subUnion = rset()
        for j in closed:
            if j<i:
                subUnion = subUnion | j
        if subUnion != i:
            jI += [i]
    return rset(jI)

#Returns the set A' where A' = A U {y: x->y is in the binary dependencies}
def binaryImplications(A, binaries):
    implied = set(A)
    for a in A:
        for B in binaries:
                if a in B[0]:
                    implied = implied | B[1]
    return implied

def findCBasis(S, PS, closed, permutation=1):

    implies = createmap(S, PS, closed)
    cbasis = []
    for i in implies:
        if critical(i[0], closed, dict(implies)):
            cbasis.append(i)

    if permutation == 1:
        cbasis.sort(key=value)
    elif permutation == 2:
        random.shuffle(cbasis)

    tries = deriveClosure(S, PS, cbasis)

    return cbasis, tries

def findUBasis(cbasis):
    ubasis = []
    for A in cbasis:
        for b in A[1]:
            ubasis.append(imp([A[0], rset([b])]))
    return ubasis

def findCDUBasis(ubasis):
    #Compute the unit basis

    cdubasis = set(ubasis)
    extra = set()
    first = True
    loops=0
    #F7 for all A -> b and C + b -> d with d != b, add A + C -> d to E Note: b not in A and d not in A+C
    while first or len(extra-cdubasis)>0:
        first = False
        loops+=1

        cdubasis = cdubasis | extra
        for A in cdubasis:
            for Cb in cdubasis:
                if A[1] < Cb[0] and not Cb[1]<=A[0]:     #if {b}<Cb and {d} !< A
                    extra.add(imp([A[0]|Cb[0]-A[1], Cb[1]]))

    extra = set()
    #F8 for all A -> b and C -> b, if C < A then delete A -> b from E.
    for A in cdubasis:
        for C in cdubasis:
            if C[0]<A[0] and A[1]==C[1]:
                extra.add(A)
    cdubasis = cdubasis - extra

    return list(cdubasis)#, loops

def findDBasis(cdubasis):
    '''
    the D-basis is obtained from any
direct unit basis by removing implications X ->x, for which X is not a minimal
cover of x and |X| > 1. In particular, the binary part of the direct basis, i.e.,
implications of the form y -> x, remain to be in the D-basis.
    '''
    dbasis = set(cdubasis)
    binary = set()
    nb = set()
    for i in cdubasis:
        if len(i[0])==1:
            binary.add(i)
        else:
            nb.add(i)
    #for X1 -> x and X2 -> x: for each x1 in X1 check whether x2 -> x1 is among binary implications, for some x2 in X2.
    for X1 in nb:
        for X2 in nb:
            if X1[1]==X2[1] and X1 != X2 and X1 in dbasis and X2 in dbasis:
                if X1[0] < binaryImplications(X2[0], binary):
                    #print str(X1) +" replaces " +str(X2)
                    dbasis.remove(X2)
    #We could stay within this time, choosing the minimal, with respect to
    #containment, subset X1 among all those X2 that satisfy X1 << X2 << X1. Should never apply for a CDU Basis.
    return sorted(list(dbasis), key = value)


def directD(cbasis):
    #Compute the unit basis
    dbasis = set()
    for A in cbasis:
        for b in A[1]:
            dbasis.add(imp([A[0], rset([b])]))

    binary = set()
    nb = set()
    for i in dbasis:
            if len(i[0])==1:
                binary.add(i)
            else:
                nb.add(i)
    premises = {}
    for A in nb:
        if not A[1] in premises:
            premises[A[1]]=[]
        premises[A[1]] += [A[0]]

    extra = set()
    first = True
    loops = 0
    #F7 for all A -> b and C + b -> d with d != b, add A + C -> d to E Note: b not in A and d not in A+C
    while first or len(extra-dbasis)>0:
        first = False
        loops+=1

        dbasis = dbasis | extra
        for A in dbasis:
            for Cb in dbasis:
                if A[1] < Cb[0] and not Cb[1]<=A[0]:     #if {b}<Cb and {d} !< A
                    candidate = imp([A[0]|Cb[0]-A[1], Cb[1]])
                    passes = True
                    implied = binaryImplications(candidate[0], binary)
                    #For candidate A U B -> x check that for all C ->x not C<<A U B
                    for premise in premises[candidate[1]]:
                        if premise <= implied:
                            passes = False
                    if passes:
                        print str(candidate)+" added"
                        extra.add(candidate)
                        premises[candidate[1]]+=[candidate[0]]

    #Ensure that result is a dbasis
    for i in dbasis:
            if len(i[0])==1:
                binary.add(i)
            else:
                nb.add(i)
    for X1 in nb:
        for X2 in nb:
            if X1[1]==X2[1] and X1 != X2 and X1 in dbasis and X2 in dbasis:
                if X1[0] < binaryImplications(X2[0], binary):
                    #print str(X1) +" replaces " +str(X2)
                    dbasis.remove(X2)

    return sorted(list(dbasis), key = value), loops

def findEBasis(dbasis, cl):
    ebasis = set(dbasis)
    nb = [] #non-binary implications
    eqcl=False
    for X in dbasis:
        if len(X[0])>1:
            nb += [X]
    for A in nb:
        for B in nb:
            if not A==B and len(A[1]&B[1])==1: #if two implication share a RHS
                if A in ebasis and (A[0]|cl[A[0]])>(B[0]|cl[B[0]]): #if A's closure is a superset of B's
                    ebasis.remove(A)
                elif A in ebasis and (A[0]|cl[A[0]])==(B[0]|cl[B[0]]): #if A's closure is identical to B's
                    print "Two equivalent closures: "+str(A)+" "+str(B)
                    eqcl = True
    return sorted(list(ebasis), key = value)

def findEBasis2(dbasis, cl): #Optimizes Binary and non-Binary parts of the basis
    ebasis = set(dbasis)
    for A in dbasis:
        for B in dbasis:
            if not A==B and len(A[1]&B[1])==1: #if two implication share a RHS
                if A in ebasis and (A[0]|cl[A[0]])>(B[0]|cl[B[0]]): #if A's closure is a superset of B's
                    ebasis.remove(A)
                #if A in ebasis and (A[0]|cl[A[0]])==(B[0]|cl[B[0]]): #if A's closure is a superset of B's
                    #print "Two equivalent closures: "+str(A)+" "+str(B)
    return sorted(list(ebasis), key = value)

def cycleFree(dbasis):
    nb = [] #non-binary implications
    for X in dbasis:
        if len(X[0])>1:
            nb += [X]
    # xDy if y->z1,z2,x,z3...
    for A in nb:
        x = A[1]
        dImps = set(A[1])
        change = True
        while change:
            change = False
            for B in nb:
                if len(dImps & B[1])>0: #some element from the implications is in the RHS
                    change = not B[0] < dImps
                    dImps = dImps | B[0]
                    if x < B[0]: #We derived x, hence a cycle
                        #print str(x) +" implies " + str(dImps)
                        return False
    return True

def cycleFree2(dbasis): #includes binary implications
    # xDy if y->z1,z2,x,z3...
    for A in dbasis:
        x = A[1]
        dImps = set(A[1])
        change = True
        while change:
            change = False
            for B in dbasis:
                if len(dImps & B[1])>0: #some element from the implications is in the RHS
                    change = not B[0] < dImps
                    dImps = dImps | B[0]
                    if x < B[0]: #We derived x, hence a cycle
                        #print str(x) +" implies " + str(dImps)
                        return False
    return True


def findInorderable():

    while True:
        source, ps, origClosures = generate(6,random.randint(4,12))
        source, ps, closures = standardize(source, ps, origClosures)
        cbasis = findCBasis(source, ps, closures)[0]
        ubasis = findUBasis(cbasis)
        canOrder, out = orderable(cbasis)
        #tOrder = transposeOrderable(source, ps, ubasis, [])
        if not canOrder:
            dbasis = findDBasis(findCDUBasis(ubasis)[0])
            if True: #cycleFree(dbasis) or len(closures)<13:
                print "Domain: " + str(source)
                print "Closed Sets: " + str(closures)
                print "Canonical Basis: " + str(cbasis)
                print "Canonical Unit Basis: " + str(ubasis)
                print "Specific Cycle: "+out
                print "D-Basis: "+str(dbasis)
                print "D-Cycle: "+str(not cycleFree(dbasis))
                print "Length: "+str(len(closures))
                print ""

def findUniqueClosures():

    source, ps, origClosures = generate(6,random.randint(5,10))
    source, ps, closures = standardize(source, ps, origClosures)


    #source = rset([1,2,3,4,5,6])
    #ps = powerset(source)
    #closures = freeze([[],[1],[1,2],[1,3],[1,3,6],[1,3,6,2],[4],[4,5],[1,3,4],[1,3,4,6],[1,3,4,5,6],[1,2,3,4,5,6]]) # original test
    #closures = freeze([[2, 3], [3], [5], [2], [1, 3, 4], [], [2, 4], [4], [1, 2, 3, 4, 5], [1, 2, 4, 5], [2, 4, 5], [1, 4], [3, 5]]) #1st exception reduced
    #closures = freeze([[2, 4], [5], [3], [], [4], [1, 2, 5], [1, 3, 4], [2, 3], [1], [2], [1, 2, 3, 4, 5]]) #2nd exception
    #closures = freeze([[5], [3], [], [4], [1, 2, 5], [1, 3, 4], [1], [2], [1, 2, 3, 4, 5]]) #2nd modified
    #closures = freeze([[1], [1, 3], [2], [1, 5], [1, 2, 3, 4, 5], [], [1, 2, 4]])
    #closures = freeze([[2, 4], [5], [6], [], [1, 2, 3, 4], [2, 3, 6], [1, 2, 3, 4, 5, 6], [1], [2, 3], [1, 5, 6], [2, 4, 5], [2], [1, 6]]) #no single pass ordering of suggested type found
    #closures = freeze([[1, 2, 4, 6], [3, 6], [2, 6], [], [4], [1, 3, 5], [1, 2, 3, 4, 5, 6], [1, 3], [2, 3, 4, 5], [2, 4], [2, 3, 6], [3], [1, 3, 6], [1, 4], [3, 5], [6], [2, 3], [1, 6], [1], [2]]) #*no* single pass ordering
    #closures = freeze([(1, 2), (), (1, 2, 4), (1, 3, 5), (1, 2, 3, 4, 5, 6), (1, 3), (1, 3, 4, 6), (2, 5, 6), (5,), (3,), (6,), (2,), (1, 4), (2, 3), (1, 6), (1,), (1, 2, 3)]) #actually unsortable ubasis
    #ubasis = [imp([rset([2,5]),rset([6])]), imp([rset([4]),rset([1])]), imp([rset([1,5]),rset([3])]), imp([rset([3,6]),rset([4])]), imp([rset([3,5]),rset([1])]), imp([rset([3,6]),rset([1])]), imp([rset([1,4,6]),rset([3])]), imp([rset([1,3,4]),rset([6])]), imp([rset([2,6]),rset([5])]), imp([rset([5,6]),rset([2])])]

    #closures = rset([x.intersection(source) for x in closures]) #reduce the set

    implies = createmap(source, ps, closures)
    jI = joinIrreducible(closures)
    cbasis, tries = findCBasis(source, ps, closures)
    ddbasis, DLoops = directD(cbasis)
    ubasis = findUBasis(cbasis)
    cdubasis, CDLoops = findCDUBasis(ubasis)
    dbasis = findDBasis(cdubasis)
    free = cycleFree(dbasis)
    free2 = cycleFree2(dbasis)
    ebasis = findEBasis(dbasis, dict(implies))
    ebasis2 = findEBasis2(dbasis, dict(implies))
    ord, result = orderable(source, ps, ubasis)

    #print ord
    #print result

    '''
    tries = 2
    runs = 0
    while tries > 1 and runs <100000:
        runs +=1
        random.shuffle(ubasis)
        ubasis=sorted(ubasis, key=value)
        tries = deriveClosure(source, ps, ubasis)


    #ubasis=sorted(ubasis, key=value)
    '''
    '''
    #Will search for an unsortable cbasis that resists 10000 random sorts
    runs = 0
    while runs!=1000000:
        tries = 1
        while tries == 1:
            source, ps, closures = generate(6,random.randint(4,10))
            implies = createmap(source, ps, closures)
            jI = joinIrreducible(closures)
            cbasis, tries = findCBasis(source, ps, closures)
            ubasis = []
            for A in cbasis:
                for b in A[1]:
                    ubasis.append(imp([A[0], rset([b])]))
            tries = deriveClosure(source, ps, ubasis)

        runs = 0
        while tries > 1 and runs <1000000:
            runs +=1
            random.shuffle(ubasis)
            tries = deriveClosure(source, ps, ubasis)
    '''

    #if not free:# or len(ubasis)>=len(ebasis):
    #    return False

    print "Domain: " + str(source)
    #print "Power Set: " + str(ps)
    print "Closed Sets: " + str(closures)
    print "Original Closed Sets: " + str(origClosures)
    #print "Implication Basis: " + str(implies)
    print "Join Irreducible Sets: " + str(jI)
    print "Canonical Basis: " + str(cbasis)
    print "Unit Can. Basis: " + str(ubasis)
    print "CDUI Basis: " + str(cdubasis)
    print "Direct D-Bases: "+str(ddbasis)
    print "DBasis: " + str(dbasis)
    print "EBasis: " + str(ebasis)
    print "Binary optimized EBasis: " + str(ebasis2)
    print "Non-Binary Cycle Free: " + str(free) +". Cycle Free: " + str(free2)
    print "CBasis :"+str(len(cbasis))+" EBasis: "+str(len(ebasis))+" CUBasis :"+str(len(ubasis))
    print "CDUI loops: "+str(CDLoops)+" D-Basis loops: "+str(DLoops)
    print tries

    return True

def compareChaining():
    totalSize =0
    chainOps = 0
    dOps =0
    for i in range(2):#range(10**5):
        source, ps, closures = generate(4,random.randint(4,10))
        cbasis, tries = findCBasis(source, ps, closures)
        ubasis = findUBasis(cbasis)
        cdubasis = findCDUBasis(ubasis)
        dbasis = findDBasis(cdubasis)
        premise = random.choice(list(ps))
        chainOps += chainResolve(dbasis, premise)[1]
        dOps += dDerive(dbasis, premise)[1]

        print str(closures)
        print str(cdubasis)

        for A in dbasis:
            totalSize+=len(A[0])
            totalSize+=len(A[1])


        #print "Premise: "+str(premise)
        #print "DBasis: "+str(dbasis)
        #print "DBasis Length: "+str(len(dbasis))
    print "Total Implication Lengths: "+str(totalSize)
    print "Chain Operations: "+str(chainOps)
    print "DBasis Operations: "+str(dOps)

def efficiencyTest():
    '''
    Variables
    Size of domain: 5 - 9
    (Subsets used to generate closure: 3-7)
    Closure size: 5 - 35
    Left Hand Side (Starting key): 1-7
    Basis Sizes
    '''

    print 10**3
    totalnum = {}
    cutotal = {}
    dtotal = {}
    cdutotal = {}
    for its in range(10**6):
        print its
        source, ps, closures = generate(5,random.randint(3,8))
        cbasis, tries = findCBasis(source, ps, closures)
        csize = len(closures)
        ubasis = findUBasis(cbasis)
        cdubasis = findCDUBasis(ubasis)
        dbasis = findDBasis(cdubasis)
        #lhs = rset()
        #while len(lhs)!=3:
        lhs = random.choice(list(ps))
        lhsize = len(lhs)
        res1, cutime = deriveImplications(ubasis, lhs)
        #res2, cdutime = deriveImplications(cdubasis, lhs)
        #res3, dtime = deriveImplications(dbasis, lhs)
        if not (csize, lhsize) in totalnum:
            totalnum[csize, lhsize] = 0
            cutotal[csize, lhsize] = 0
            dtotal[csize, lhsize] = 0
            cdutotal[csize, lhsize] = 0
        totalnum[csize, lhsize] +=1
        cutotal[csize, lhsize] += cutime
        dtotal[csize, lhsize] += len(dbasis)
        cdutotal[csize, lhsize] += len(cdubasis)

    for x in range(0,40):
        for y in range(0,20):
            if (x, y) in totalnum:
                n = totalnum[x,y] + 0.0
                print "Closure size: "+str(x)+" LHS size: "+str(y)
                print "Tests: " + str(n)
                print "Canonical Unit Time "+str(cutotal[x,y]/n)
                print "Canonical Direct Unit Time "+str(cdutotal[x,y]/n)
                print "D-Basis Time "+str(dtotal[x,y]/n)
                print ""


#findInorderable()
#compareChaining()

## Sandbox Stuff ##
def parseClosure(closureFileName):
    inputFile = open(closureFileName, "r")
    inputString = inputFile.read().strip()[1:-1] # strip first set of brackets
    inputList = inputString.split(", ")
    intInputList = []
    maxElem = 0
    for setString in inputList:
        intMooreSet = []
        for element in setString[1:-1].split(","):
            if element: # parse if string nonempty
                intElem = int(element.strip())
                intMooreSet.append(intElem)
                if intElem > maxElem:
                    maxElem = intElem
        intInputList.append(intMooreSet)

    return intInputList, maxElem

inputList, maxElement = parseClosure("./madeUpFamily.txt")
baseSet = rset(range(1,maxElement+1))

powSet = powerset(baseSet)
#mooreFam = freeze([[], [1], [2], [3], [4], [1,2], [1,3], [2,3,4], [4,5], [1,2,3,4,5]])
mooreFam = freeze(inputList)
#source, ps, closures = closure(mooreFam, baseSet, powSet)
cbasis, tries = findCBasis(baseSet, powSet, mooreFam)#findCBasis(source, ps, closures)
ubasis = findUBasis(cbasis)
cdubasis = findCDUBasis(ubasis)

print str(cdubasis)

outputFile = open("./madeUpBasis.txt", "w")
outputFile.write(str(cdubasis))
