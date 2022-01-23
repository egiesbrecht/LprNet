# LprNet
An implementation of the prNet project to analyze the German language

- [About](#About)
- [prNet](#The-integration-of-prNet)
- [The database](#The-database)

## About
LprNet uses the prNet-project to create a simple possibility to analyze the German language. The goal of this project is to test out how far pattern recognition can go with language-like constructs.

## The integration of [prNet](#https://github.com/egiesbrecht/prNet)
This project isn't directly a fork nor uses an upstream repository. Instead it just uses a local copy of prNet's files. I decided to go this way because some small changes were needed that would make sense in the original repo. These changes don't effect the core functionality but rather some practical use cases.

## The database
I've used parts of the databases of the [Morphy-Project](https://euralex.org/elx_proceedings/Euralex2000/071_Wolfgang%20LEZIUS_Software%20Demonstration_Morphy%20German%20Morphology,%20Part-of-Speech%20Tagging%20and%20Applications.pdf) and [Open-Thesaurus](https://www.openthesaurus.de/) to create simple dictionary and associations tables into a SQLite database. Those are only used intern in the project to create objects which contain all necassary information to analyze whole articles and similar sorts of texts.
The tables *dictionary*, *associations* and *texte* are required to run the program, all other tables will automaticly be created when they are needed. The here given sample also contains examples, created by a single run of the *src/demo/language/TranslateTexts.java* class.
