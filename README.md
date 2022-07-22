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
* [x] Users can log in
* [x] Use current location and default radius or set a location and radius
* [ ] Tinder-style swiping animation
  * [x] Select yes or no for each restaurant
* [ ] Pictures, info, and links in detail view of restaurants
* [x] All the yes's go into saved tab
* [ ] Filtering in search: Let the users select prices, food category, rating, number of reviews.
* [x] Able to plan out a path to your destination to only show places along the way
   * [x] optimize the search query to load restaurants along the path when the current list is exhausted so too many restaurants are not queried at once.
* [ ] Map view to show where the restaurant is 
* [x] Use the Model–View–ViewModel architectural pattern to organize the code
* [x] Use state flows to update the UI when the data changes

**Optional Nice-to-have Stories**
(more focused on planning/sharing)
* [ ] Show directions to restaurant
* [ ] After eating there, mark if you would/wouldn't go again to either save into a favorites folder or delete from the current folder
* [ ] Add an overall map view to your saved restaurants
* [ ] Able to color code map pinpoints
* [ ] Able to filter map by category
* [ ] Sort saved restaurants in folders by location
* [ ] Follow friends' accounts to see their favorite restaurants
* [ ] Use data from what the user liked to recommend a restaurant near you using push notifications
   * [ ] See the user's saved restaurants, skipped restaurants, and favorited restaurants (ones they've tried and marked that they like) and create a weighting system of the types of restaurants that the user prefers.
   * [ ] This will be based on types of cusine, reviews, and price range.
   * [ ] Find restaurants, whether that be ones they do not know about or ones that are already on their list but have not tried yet, that it feels the user would especially like and recommend them a restaurant with a push notification if they are ever close to the area.

**Complex Features + ideas to execute them**
1. Set a destination from your current location to find restaurants along the way
   * Use Google Maps API
   * Find a path to get to your destination and set multiple small radii along those lines and show restaurants within those radii.
2. Optimize the search query 
   * Only add query additional restuarants along the path as the user exhausts the list instead of querying everything along the path.
5. (Stretch goal complex feature) Add background thread prefetching 
   * A problem I have was that the saved/skipped restaurants lists were not loaded initially before fetching the restaurants. For now, I went around this issue by loading the saved fragment first before changing tabs to the restaurants fragment. However, I want to try loading it in the background so that the first screen can be the restaurants screen, since it is the main screen of my app.

   
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
* Saved 
   * All restaurants saved
* Profile/Settings

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Restaurants
* Saved
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
