
![Logo](https://i.ibb.co/CKRvwtxd/image.png)

# Lolita Store - Java Apache NeatBeans

An inventory and point-of-sale system for Lolita Store developed in Java using NetBeans and JDK 23. Various data structures are applied to handle data efficiently. The following modules and their respective data structures are used, with explanations of why they were chosen:

## Sales Module
-Binary Search Tree (BST) for Products: 
Used for fast product searches by Product Code to quickly generate a sale.

-Queues for Bill Generation (FIFO):
 A queue is used to manage the bill generation process. Since bill creation can take some time, the queue ensures that sales are processed in order of arrival, allowing the system to handle multiple sales efficiently.

-Arrays for Shopping Cart: 
Arrays are used because they provide an efficient way to add elements and access them by index. This helps in removing selected items when a customer interacts with the UI, making the process smoother.

## Customers Module

-Singly Linked List for Customers: Chosen for its efficiency in adding and removing elements without shifting others, making it ideal for dynamic operations where customer data is frequently updated.

#Suppliers Module
-Singly Linked List for Suppliers: Similar to the customers module, a singly linked list is used because it allows efficient addition and removal of suppliers without shifting other elements, which is optimal for dynamic operations.

#Products Module
-Binary Search Tree (BST) for Products: The BST is used again for fast product searches. Additionally, different tree traversal techniques such as inorder, preorder, and postorder are applied to display data in the UI.

#Sales History Module
-Stacks for Sales (LIFO): A stack is employed to handle the sales history. If there is a mistake in a sale, the most recent sale can be easily removed using the Last In, First Out (LIFO) principle, allowing quick error correction.

#General Home Module
-Double Matrix for Privileges Configuration: A double matrix is used to store and manage user roles and privileges in an organized way, allowing easy access and modification of permissions based on user roles.

## Licences


[![UTP License](https://img.shields.io/badge/License-UTP-red.svg)](https://utp.edu.pe/)



## Authors

- [@EdderRamos](https://www.github.com/EdderRamos)


## Screenshots

![Login](https://i.ibb.co/qF4m4PwT/image.png)
![Home](https://i.ibb.co/cX67xQ6v/image.png)
![Customers](https://i.ibb.co/B2Bn3XNW/image.png)
![Products](https://i.ibb.co/Zzdr333K/image.png)
![Providers](https://i.ibb.co/qL2CJ81W/image.png)
![Record](https://i.ibb.co/KjRhrVJN/image.png)
![Add User](https://i.ibb.co/mFF90zPS/image.png)


## Technologies

![Java](https://img.shields.io/badge/Java-JDK%2023-orange)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![NetBeans](https://img.shields.io/badge/NetBeans-IDE-green)

Data Structures: Binary Search Tree, Queues, Arrays, Singly Linked List, Stacks, Double Matrix