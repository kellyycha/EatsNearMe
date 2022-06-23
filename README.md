Original App Design Project - README
===

# Eats Near Me

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app will help you decide on the best places to eat, to make the most out of your spontaneous trips. If you have downtime in a random city and want to visit a good restaurant that is also nearby, it can be hard to research and choose the best one right then and there. So, using a dating app style for restaurants, you can quickly see some pictures and basic information of each restaurant to determine if it will be the right fit for you. If you want to find places to visit in advance, you can also set your location to other areas to see whats around there.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Food, Planning
- **Mobile:**
- **Story:**
- **Market:**
- **Habit:**
- **Scope:**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**
(more focused on spontaneous)
* Users can log in
* Set location and radius
* Tinder-style swiping animation
  * Swipe yes or no for each restaurant
* Pictures, info, and links in detail view of restaurants
* All the yes's go into folder according to location
* Categorize by types of food 
* Able to plan out a path to your destination to only show places along the way
* After eating there, mark if you would/wouldn't go again to either save into a favorites folder or delete from the current folder
* Use data from what the user liked to recommend a restaurant near you using push notifications

**Optional Nice-to-have Stories**
(more focused on planning/sharing)
* Map of all locations
* Show directions to the restaurant
* Able to color code map pinpoints
* Able to filter map by category
* Able to choose which folder to go to and name it
* Follow friends' accounts to see their favorite restaurants
* implicit intent to open map app to give directions

### 2. Screen Archetypes

* Log In
   * Users can log in
* Restaurants Screen
   * Card view of restaurants to swipe through
   * Can set radius, location, type of food
* Restaurants Detail view
   * All info on resaurant
* Filters
   * Categorize searches
* Location Folders
   * Folders of restaurants
* Location Folder contents
   * All restaurants per folder
* Map
  * Saved places on a map
* Profile/Settings

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Restaurants
* Folders
* Map
* Profile/Settings

**Flow Navigation** (Screen to Screen)

* Log in screen
* Details screen
  * Click on restaurant card
* Filters screen
* Location folder contents
* Map color coordination button

## Wireframes
<img width="495" alt="Screen Shot 2022-06-16 at 10 34 02 AM" src="https://user-images.githubusercontent.com/65841983/174132039-3eb18aa3-12b3-4eff-99d2-79a5cd4399f2.png"> <img width="495" alt="Screen Shot 2022-06-16 at 10 34 21 AM" src="https://user-images.githubusercontent.com/65841983/174132049-b46f20e0-289e-4380-a067-21f8896cbf79.png">
<img width="495" alt="Screen Shot 2022-06-16 at 10 34 29 AM" src="https://user-images.githubusercontent.com/65841983/174132061-d2e6201d-0c56-434e-b620-09b060e8481d.png">
<img width="495" alt="Screen Shot 2022-06-16 at 10 34 34 AM" src="https://user-images.githubusercontent.com/65841983/174132072-dc2ae943-5503-4f17-ae54-c3293a079773.png">
<img width="495" alt="Screen Shot 2022-06-16 at 10 34 38 AM" src="https://user-images.githubusercontent.com/65841983/174132079-6b12d974-c208-42e6-bd20-7b4d7ec5339f.png">
<img width="607" alt="Screen Shot 2022-06-16 at 10 34 45 AM" src="https://user-images.githubusercontent.com/65841983/174132083-3babfa7a-1436-4cd0-9b51-f9f03c320be2.png">

## Schema 

### Models

Restaurant Details
| Property         | Type     | Description                  |
| ---------------- | -------- | ---------------------------- |
| restaurantId     | String   | Unique ID for restaurant     |
| restaurantName   | String   | Name of Restaurant           |
| rating           | Number   | Yelp rating of restaurant    |
| priceRange       | String   | Dollar sign range            |
| category         | String   | Type of food                 |
| restaurantTimes  | String   | Times reataurant is open     |
| menu             | File     | Restaurant menu              |
| image            | File     | Pictures of food             |
| reviews          | String   | Reviews of restaurant        |
| restaurantAddress| String   | Address of Restaurant        |
| phoneNumber      | Number   | Phone number of restaurant   |
| website          | String   | Website of restaurant        |

User Details
| Property         | Type     | Description                  |
| ---------------- | -------- | ---------------------------- |
| user             | Pointer  | User of the app              |
| location         | String   | User location or address     |

### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
