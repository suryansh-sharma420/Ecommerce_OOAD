import requests
from PIL import Image
from io import BytesIO
import os

# Image URLs from free stock photo sites
image_urls = {
    # Electronics
    'laptop.jpg': 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853',
    'smartphone.jpg': 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9',
    'headphones.jpg': 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e',
    'smartwatch.jpg': 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0',
    'tablet.jpg': 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0',
    'camera.jpg': 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32',
    'console.jpg': 'https://images.unsplash.com/photo-1486572788966-cfd3df1f5b42',
    'earbuds.jpg': 'https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46',
    
    # Clothing
    'tshirt.jpg': 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab',
    'jeans.jpg': 'https://images.unsplash.com/photo-1542272604-787c3835535d',
    'hoodie.jpg': 'https://images.unsplash.com/photo-1556821840-3a63f95609a7',
    'shorts.jpg': 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b',
    'jacket.jpg': 'https://images.unsplash.com/photo-1551488831-00ddcb6c6bd3',
    'dress-shirt.jpg': 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf',
    'running-shoes.jpg': 'https://images.unsplash.com/photo-1542291026-7eec264c27ff',
    'sneakers.jpg': 'https://images.unsplash.com/photo-1549298916-b41d501d3772',

    # Books
    'programming-book.jpg': 'https://images.unsplash.com/photo-1517842645767-c639042777db',
    'mystery-book.jpg': 'https://images.unsplash.com/photo-1474932430478-367dbb6832c1',
    'cooking-book.jpg': 'https://images.unsplash.com/photo-1544947950-fa07a98d237f',
    'history-book.jpg': 'https://images.unsplash.com/photo-1583468982228-19f19164aee2',
    'scifi-book.jpg': 'https://images.unsplash.com/photo-1518770660439-4636190af475',
    'business-book.jpg': 'https://images.unsplash.com/photo-1507842217343-583bb7270b66',
    'poetry-book.jpg': 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c',
    'selfhelp-book.jpg': 'https://images.unsplash.com/photo-1544716280-aa053eb1c1f3',
    
    # Placeholder
    'placeholder.jpg': 'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d'
}

def download_and_optimize_image(url, filename):
    try:
        # Download image
        response = requests.get(url)
        response.raise_for_status()
        
        # Open image with PIL
        img = Image.open(BytesIO(response.content))
        
        # Convert to RGB if necessary
        if img.mode in ('RGBA', 'LA') or (img.mode == 'P' and 'transparency' in img.info):
            background = Image.new('RGB', img.size, (255, 255, 255))
            background.paste(img, mask=img.split()[-1])
            img = background
        
        # Resize image to 500x500 while maintaining aspect ratio
        img.thumbnail((500, 500), Image.LANCZOS)
        
        # Save as optimized JPG
        img.save(filename, 'JPEG', quality=85, optimize=True)
        
        # Check file size
        file_size = os.path.getsize(filename) / 1024  # Size in KB
        if file_size > 200:
            # If still too large, reduce quality
            img.save(filename, 'JPEG', quality=70, optimize=True)
        
        print(f"Successfully processed {filename}")
    except Exception as e:
        print(f"Error processing {filename}: {str(e)}")

def main():
    # Create images directory if it doesn't exist
    os.makedirs('src/main/resources/images', exist_ok=True)
    
    # Download and optimize each image
    for filename, url in image_urls.items():
        output_path = os.path.join('src/main/resources/images', filename)
        download_and_optimize_image(url, output_path)

if __name__ == "__main__":
    main() 