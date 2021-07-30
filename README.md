# Hyperboard

Anonymous blogging software written using Java with Spring Boot, skeleton.css and PostgreSQL.

# Usage

Hyperboard is anonymous blogging software inspired by imageboards, which are a type of anonymous internet forum based on the sharing of images and text. 

# Installation

Follows normal java build process using maven. Install postgres first, then package a jar. You can run the jar as a service on linux. I personally recommend using Nginx or something similar as a reverse proxy for SSL/TLS/HTTPS purposes as well as for optional performance/caching/microcaching reasons. If you run into errors try doing a maven clean and then repackaging. Check application.properties for the relevant database connection information. script autoinserts the admin user if it doesn't exist, and public/uploads and public/thumbs folders are automatically created as well. Make sure to demote 'admin' account to a regular user after creating a real admin account.