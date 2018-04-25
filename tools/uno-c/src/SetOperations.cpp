#include <vector>
#include <iostream>
#include <cstdlib>
#include <algorithm>
#include "timestamp.h"

std::vector<int> & complement(std::vector<int> & subset, std::vector<int> & superset) {
    std::vector<int> * complement = new std::vector<int>();
    for (unsigned int i = 0; i < superset.size(); i++) {
        int j = superset[i];
        if (!(std::find(subset.begin(), subset.end(), j) != subset.end())) {
            //Superset doesn't contain that element
            complement->push_back(j);
        }
    }
    return *complement;
}

std::vector<int> & intersection(std::vector<int> & set1, std::vector<int> & set2) {
    std::vector<int> * intersection = new std::vector<int>();
    for (unsigned int i = 0; i < set1.size(); i++) {
        int j = set1[i];
        if (std::find(set2.begin(), set2.end(), j) != set2.end()) { //Searches set2 for element j in set1
            intersection->push_back(j);
        }
    }
    return *intersection;
}

void printVector(std::vector<int> & path) {
    for (std::vector<int>::const_iterator i = path.begin(); i != path.end(); ++i)
        std::cout << *i << ' ';
}

void testComplement() {
    std::vector<int> * v1 = new std::vector<int>();
    std::vector<int> * v2 = new std::vector<int>();
    v1->push_back(1);
    v1->push_back(2);
    v1->push_back(3);
    v1->push_back(4);
    v1->push_back(5);
    v1->push_back(6);
    v1->push_back(7);
    v2->push_back(3);
    v2->push_back(4);
    v2->push_back(5);
    std::vector<int> & complement1 = complement(*v2,*v1);
    printVector(complement1);
    delete v1;
    delete v2;
    delete &complement1;
}

void testIntersection(){
    std::vector<int> * v1 = new std::vector<int>();
    std::vector<int> * v2 = new std::vector<int>();
    v1->push_back(1);
    v1->push_back(2);
    v1->push_back(3);
    v1->push_back(5);
    v1->push_back(6);
    v1->push_back(7);
    v2->push_back(3);
    v2->push_back(4);
    v2->push_back(5);
    
    std::vector<int> & intersection1 = intersection(*v2,*v1);
    printVector(intersection1);
    delete v1;
    delete v2;
    delete &intersection1;
}
