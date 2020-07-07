# WasteBuddy

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

# Overview
## Description
WasteBuddy is a tool created to help people sort their trash and a platform for people to share DIY projects.

## App Evaluation
- **Category:** Lifestyle/Reference
- **Mobile:** How uniquely mobile is the product experience?
    - WasteBuddy will allow user's to scan barcodes on items using their phone camera, then tell them how to dispose of the product and how the product can be reused. Additionally, users that have created an account, can post images of their DIY projects using their phone camera. The app will also be able to use the user's location to determine best practice for waste disposal, as policies can vary based on location.

- **Story:** How compelling is the story around this app once completed?

    - As more people become aware of the impacts that humans have on the environment, more people gain a sense of responsibility to help mitigate these effects. WasteBuddy is designed to help empower these people and provide a platform for them to share their ideas. Whether you just need to know where to throw something away or you want a platform to share your DIY projects with the world, WasteBuddy is here to help.
    
- **Market:** Market: How large or unique is the market for this app?

    - This application has the potential to scale to billions of users, because all humans produce waste. As the world continues to develop, and more societies begin to acknowledge the importance of effective waste management, people will need an easy-to-use tool to help them adjust to change.
    - There are two groups of users that will gain value from the app. The larger group is the set of users that just need to sort their waste. The niche group includes users who enjoy DIY projects.
    - This app also has the potential to appeal to companies that want to pay for product placement.

- **Habit:** How habit-forming or addictive is this app?

    - **[Average]** Users that are only interested in getting help with sorting waste may open the app as little as once a day. This depends greatly on how educated the user is before downloading the app. No matter their prior knowledge, these users are likely to use the app less over time as they start to remember how to sort things.
    - **[Niche]** Users that are interested in DIY may open the app as little as once a day for sorting trash, but will be more likely to stay on the app as they begin to browse DIY projects. Ideally, these users will be converted into creators when they decide to share their own DIY projects on the platform


- **Scope:** How well-formed is the scope for this app?
    - It would be very challenging to implement all of the features of this app, however a stripped down version of the app is still interesting to build and still valuable to users. The product that I want to build is crystal clear.



# Product Spec

## 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can register
* User can search for items
* User can view item details
* User can scan barcode to search
* User can browse DIY projects
* User can view other users' profiles

* User (registered) can add item to database
* User (registered) can post DIY project
* User (registered) can follow another user

**Optional Nice-to-have Stories**

* User can view DIY projects that use an item
* User profile shows their DIY projects
* User (registered) can like DIY project
* User (registered)can link items that were used in their DIY projects
* Provide item information based on user's locale

## 2. Screen Archetypes

* Login Screen
	* User can login/register
	* User can continue as guest
* Home
    * User can browse DIY projects
    * User can view other users' profiles

* Search
	* User can search for items
    * User can scan barcode to search

* Item
	* User can view item details

* Project
	* User can view DIY project details

* Creation
	* User can post a new DIY project to their feed

* User
	* User can follow another user
	* 
## 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Feed
* Search

**Flow Navigation** (Screen to Screen)

* Login Screen
	=> Home

* Home
	=> Search
	=> Project
	=> User
	
* Search
	=> Item
	=> Future version should allow for searching of projects

* Item
	=> None, but future version will allow user to see projects that use this item and navigate to those projects
	
* Creation
	=> Home

* User
	=> Project

# Wireframes

<img src="https://github.com/Amari-G/WasteBuddy/blob/master/WasteBuddy%20Wireframe.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
