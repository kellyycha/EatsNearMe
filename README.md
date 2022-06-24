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
- **Category:** Food, Planning
- **Mobile:** This app would be primarily developed for mobile due to its spontaneous nature. But would perhaps be just as viable on a computer, especially if I add more features regarding planning. Functionality wouldn't be limited to mobile devices, however mobile version could potentially have more features, such as the push notifications.
- **Story:** Retrieves data for restaurants around the area for the user to decide where to eat.
- **Market:** Any individual could choose to use this app.
- **Habit:** This app could be used as often or unoften as the user wanted depending on if they are visiting new cities or want to go out with friends and find places to eat.
- **Scope:** First we would start with spontaneous decisions and recommendations, then perhaps this could evolve into a foodie tour planning app with a map of saved locations, reminders to make reservations, and saving into custom folders. In adition, this could also become a restaurant recommendation sharing app amongst friends and popular food bloggers.

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

**Complex Features + ideas to execute them**
1. Set a destination from your current location to find restaurants along the way
   * Use Google Maps API
   * Idea 1: Find a path to get to your destination and set multiple small radii along those lines and show restaurants within those radii.
   * Since there are multiple ways to get from one place to another, only following one path would be restricting. There might be a restaurant a few streets over that won't even be too out of the way, but not within the direct path. Therefore, one solution to this can be to see if I can find all of the alternate routes and combine those radii to find retaurants within all of them. 
   * Idea 2: Determine a straight line that goes from your location to the destination and set an oval boundary between the two to find restaurants within there, so that they will still be on the way, but not on the exact/ shortest path.
   * Maybe find a way to implement both ideas so the user can toggle "fastest route" if they're in more of a rush and would rather not deviate farther than they need to from the single path.
2. Push notifications for recommended restaurants near you based on user saved restaruant data
   * See the user's saved restaurants, skipped restaurants, and favorited restaurants (ones they've tried and marked that they like) and create a weighting system of the types of restaurants that the user prefers.
   * This will be based on types of cusine, reviews, and price range.
   * Find restaurants, whether that be ones they do not know about or ones that are already on their list but have not tried yet, that it feels the user would especially like and recommend them a restaurant with a push notification if they are ever close to the area.
   
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
List of network requests by screen
* Restaurants Screen
   * (Read/GET) Get restaurant data from Yelp to display
   * (Create/POST) Save the restaurant
   * (Delete) Reject the restaurant
* Saved Restaurants Screen
   * (Read/GET) Query all the restaurants that user has saved
* Profile Screen
(Read/GET) Query logged in user object
