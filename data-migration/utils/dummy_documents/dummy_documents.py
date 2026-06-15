import boto3
import pathlib
import re
import argparse

from azure.identity import DefaultAzureCredential
from azure.storage.blob import ContainerClient
from reportlab.pdfgen.canvas import Canvas
from io import BytesIO
from PIL import Image, ImageDraw, ImageFont


def dummy_pdf(file_name):
    with BytesIO() as fh:
        canvas = Canvas(fh)
        canvas.drawString(50, 800, file_name)
        canvas.showPage()
        canvas.save()
        return fh.getvalue()


def make_image(file_name, format, background_colour, foreground_colour):
    with BytesIO() as fh:
        image = Image.new('RGB', (400, 400), color=background_colour)
        draw = ImageDraw.Draw(image)
        font = ImageFont.truetype('Courier.ttf', 10)
        draw.text((10, 10), file_name, fill=foreground_colour, font=font)
        image.save(fh, format=format)
        return fh.getvalue()


def dummy_jpeg(file_name):
    return make_image(file_name, 'JPEG', (255, 255, 255), (0, 0, 0))


def dummy_png(file_name):
    return make_image(file_name, 'PNG', (255, 255, 255), (0, 0, 0))


def placeholder(file_name):
    return make_image(f'{file_name} PLACEHOLDER', 'PNG', (0xe9, 0x1e, 0x63), (255, 255, 255))


def document_exists(file_name, aws_s3_client, bucket_name):
    response = aws_s3_client.list_objects(Bucket=bucket_name, Prefix=file_name)
    return 'Contents' in response and len(response['Contents']) == 1


def upload_document(file_name, file_content, aws_s3_client, bucket_name):
    response = aws_s3_client.put_object(
        Bucket=bucket_name, 
        Key=file_name,
        Body=file_content)
    print(f'response: {response}')


despatch = {
    '.pdf': dummy_pdf,
    '.jpg': dummy_jpeg,
    '.png': dummy_png,
}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--sa')
    parser.add_argument('--srccontainer')
    parser.add_argument('--destbucket')
    parser.add_argument('--prefix', default='GSProjectDocuments/')
    args = parser.parse_args()

    azure_container_client = ContainerClient(
        account_url=args.sa,
        container_name=args.srccontainer,
        credential=DefaultAzureCredential()
    )

    aws_s3_bucket_name = args.destbucket

    aws_s3_client = boto3.client('s3')

    blob_prefix = args.prefix
    blob_prefix_re = re.compile(f'^{blob_prefix}')

    for sc_document in azure_container_client.list_blobs(name_starts_with=blob_prefix):
        document_name = blob_prefix_re.sub('', sc_document.name)
        if document_exists(document_name, aws_s3_client, aws_s3_bucket_name):
            print(f'EXISTS: {document_name}')
            continue
        extension = pathlib.Path(document_name).suffix
        print(f'CREATE: {document_name}')    
        if extension in despatch:
            document_data = despatch[extension](document_name)
        else:
            document_data = placeholder(document_name)
        upload_document(document_name, document_data, aws_s3_client, aws_s3_bucket_name)


if __name__ == '__main__':
    main()