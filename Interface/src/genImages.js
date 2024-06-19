import axios from './axios';
import { API_PATHS } from './apiPaths';

function arrayBufferToBase64(buffer) {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    const len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
}

export const GEN_IMAGES = {
    generateVotingImage(text){
        const canvas = document.createElement('canvas');
        canvas.width = 400;
        canvas.height = 200;
        const context = canvas.getContext('2d');

        // Draw the background color
        // const fillColor = '#' + Math.floor(Math.random() * 16777215).toString(16);
        const fillColor = "#0D4EC1";
        context.fillStyle = fillColor;
        context.fillRect(0, 0, canvas.width, canvas.height);
        
        // Set the font size and style
        context.font = '40px Arial';
        context.fillStyle = '#ffffff';
        context.textAlign = 'center';
        context.textBaseline = 'middle';

        // Check if the full title fits
        let displayText = text;
        const maxWidth = canvas.width - 40; // Adjust max width to fit within the canvas

        if (context.measureText(displayText).width > maxWidth) {
            // If text is too wide, use only the first letters of each word
            displayText = text.split(' ').map(word => word.charAt(0).toUpperCase()).join('');
        }

        if (context.measureText(displayText).width > maxWidth) {
            // If text is still too wide, use only the first letter
            displayText = text.charAt(0).toUpperCase();
        }

        // Calculate the starting Y position for the text to be vertically centered
        const startY = canvas.height / 2;

        // Draw the text
        context.fillText(displayText, canvas.width / 2, startY);

        const dataURL = canvas.toDataURL('image/png');
        return dataURL;
    },
    generateAvatar(text) {
        const canvas = document.createElement('canvas');
        canvas.width = 100;
        canvas.height = 100;
        const context = canvas.getContext('2d');
    
        // Draw the background color
        // const fillColor = '#' + Math.floor(Math.random() * 16777215).toString(16);
        const fillColor = "#0D4EC1";
        context.fillStyle = fillColor;
        context.fillRect(0, 0, canvas.width, canvas.height);
    
        // Set the font size and style
        context.font = '40px Arial';
        context.fillStyle = '#ffffff';
        context.textAlign = 'center';
        context.textBaseline = 'middle';
    
        // Check if the full title fits
        let displayText = text.charAt(0).toUpperCase();
        const maxWidth = canvas.width - 20; // Adjust max width to fit within the canvas
    
        // Calculate the starting Y position for the text to be vertically centered
        const startY = canvas.height / 2;
    
        // Draw the text
        context.fillText(displayText, canvas.width / 2, startY);
    
        const dataURL = canvas.toDataURL('image/png');
        return dataURL;
    },
    async createAvatarUrl(filePath) {
        try {
            const fileUrl = API_PATHS.getImageUrl(filePath);
            const response = await axios.get(fileUrl, { responseType: 'arraybuffer' });
    
            // Convert the ArrayBuffer to a Base64 string
            const base64String = arrayBufferToBase64(response.data);
            const mimeType = response.headers['content-type'];
            const avatarUrl = `data:${mimeType};base64,${base64String}`;
            
            return avatarUrl;
        } catch (error) {
            console.error(error);
            return '';
        }
    }
}